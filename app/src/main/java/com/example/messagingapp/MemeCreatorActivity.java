package com.example.messagingapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemeCreatorActivity extends AppCompatActivity {

    private Uri selectedImageUri = null;
    private Uri selectedAudioUri = null; // New
    private String currentImageSource = "FILE";
    private String currentAudioSource = "FILE";

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ((ImageView)findViewById(R.id.imgMemePreview)).setImageURI(uri);
                }
            });

    // NEW: Audio File Explorer Launcher
    private final ActivityResultLauncher<String> pickAudio =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedAudioUri = uri;
                    Button btnAudio = findViewById(R.id.btnAudioSource);
                    btnAudio.setText("Audio Selected");
                    Toast.makeText(this, "Audio file linked!", Toast.LENGTH_SHORT).show();
                }
            });

    private String convertUriToBase64(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) baos.write(buffer, 0, len);
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) { return ""; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_creator);

        Button btnImageSource = findViewById(R.id.btnImageSource);
        Button btnPickImage = findViewById(R.id.btnPickImage);
        EditText editImageUrl = findViewById(R.id.editImageUrl);
        Button btnAudioSource = findViewById(R.id.btnAudioSource);
        EditText editAudioUrl = findViewById(R.id.editAudioUrl);
        EditText editTop = findViewById(R.id.editTopText);
        EditText editBottom = findViewById(R.id.editBottomText);
        EditText editSubtitles = findViewById(R.id.editSubtitles);

        btnImageSource.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Local File");
            popup.getMenu().add("API URL");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Local File")) {
                    currentImageSource = "FILE";
                    btnPickImage.setVisibility(View.VISIBLE);
                    editImageUrl.setVisibility(View.GONE);
                } else {
                    currentImageSource = "API";
                    btnPickImage.setVisibility(View.GONE);
                    editImageUrl.setVisibility(View.VISIBLE);
                }
                return true;
            });
            popup.show();
        });

        btnPickImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

        btnAudioSource.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Local File (Explorer)");
            popup.getMenu().add("API URL");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Local File (Explorer)")) {
                    currentAudioSource = "FILE";
                    editAudioUrl.setVisibility(View.GONE);
                    pickAudio.launch("audio/*"); // Opens file explorer
                } else {
                    currentAudioSource = "API";
                    editAudioUrl.setVisibility(View.VISIBLE);
                }
                return true;
            });
            popup.show();
        });

        findViewById(R.id.btnSave).setOnClickListener(v -> processAction(null, editTop, editBottom, editSubtitles, editImageUrl, editAudioUrl));
        findViewById(R.id.btnSendMessage).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Receiver Name");
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("Send", (dialog, which) -> processAction(input.getText().toString(), editTop, editBottom, editSubtitles, editImageUrl, editAudioUrl));
            builder.show();
        });
    }

    private void processAction(String receiver, EditText top, EditText bottom, EditText subs, EditText imgUrl, EditText audU) {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sender = prefs.getString("last_logged_in_user", "Guest");

                JSONObject memeJson = new JSONObject();
                memeJson.put("toptext", top.getText().toString());
                memeJson.put("bottomtext", bottom.getText().toString());
                memeJson.put("subtitles", subs.getText().toString());
                
                // 1. Get the path for LOCAL DB (Small string)
                String localImgPath = currentImageSource.equals("FILE") && selectedImageUri != null ? 
                        selectedImageUri.toString() : imgUrl.getText().toString();
                memeJson.put("imagedirectory", localImgPath);

                String audioData = currentAudioSource.equals("FILE") && selectedAudioUri != null ? 
                        selectedAudioUri.toString() : audU.getText().toString();
                memeJson.put("audioname", audioData);

                // 2. Save to Local Database (Uses URI, so it's safe and won't crash)
                Meme memeObj = new Meme(memeJson.toString(), this);
                Message localMsg = new Message(sender, receiver != null ? receiver : "Draft", memeObj);
                AppDatabase.getDb(this).MessageDao().insertMessage(localMsg);

                if (receiver != null) {
                    // 3. Prepare for SERVER (Convert to Base64 ONLY for the network call)
                    if (currentImageSource.equals("FILE") && selectedImageUri != null) {
                        memeJson.put("imagedirectory", convertUriToBase64(selectedImageUri));
                    }

                    JSONObject packageJson = new JSONObject();
                    packageJson.put("sender", sender);
                    packageJson.put("receiver", receiver);
                    packageJson.put("meme", memeJson);

                    URL url = new URL("http://192.168.0.150:5000/api/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) { 
                        os.write(packageJson.toString().getBytes("utf-8")); 
                    }
                    
                    if (conn.getResponseCode() == 200) {
                        runOnUiThread(() -> Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show());
                    }
                }
                finish();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
}
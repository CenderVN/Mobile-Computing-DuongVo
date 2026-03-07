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
import androidx.core.content.FileProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import android.content.Intent;

public class MemeCreatorActivity extends AppCompatActivity {

    private Uri selectedImageUri = null;
    private File cameraFile = null;
    private String currentImageSource = "FILE";
    private String permanentLocalPath = null;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
                        permanentLocalPath = saveToInternalStorage(uri);
                        selectedImageUri = uri;
                        ((ImageView)findViewById(R.id.imgMemePreview)).setImageURI(Uri.fromFile(new File(permanentLocalPath)));
                    } catch (IOException e) {
                        Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> customCameraLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                permanentLocalPath = result.getData().getStringExtra("path");
                selectedImageUri = Uri.fromFile(new File(permanentLocalPath));
                ((ImageView)findViewById(R.id.imgMemePreview)).setImageURI(selectedImageUri);
            }
        });

    private String saveToInternalStorage(Uri uri) throws IOException {
        InputStream is = getContentResolver().openInputStream(uri);
        String filename = "sent_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getFilesDir(), filename);
        OutputStream os = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) os.write(buffer, 0, len);
        os.close();
        is.close();
        return file.getAbsolutePath();
    }

    private Uri getTmpFileUri() {
        cameraFile = new File(getFilesDir(), "cam_" + System.currentTimeMillis() + ".jpg");
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", cameraFile);
    }

    private void fetchRandomCat(EditText urlInput, ImageView preview) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.thecatapi.com/v1/images/search");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                JSONArray array = new JSONArray(response);
                String catUrl = array.getJSONObject(0).getString("url");
                
                runOnUiThread(() -> {
                    urlInput.setText(catUrl);
                    com.bumptech.glide.Glide.with(this).load(catUrl).into(preview);
                    Toast.makeText(this, "Cat found!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

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
        EditText editTop = findViewById(R.id.editTopText);
        EditText editBottom = findViewById(R.id.editBottomText);
        ImageView imgPreview = findViewById(R.id.imgMemePreview);

        btnImageSource.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Gallery");
            popup.getMenu().add("Camera");
            popup.getMenu().add("Get Random Cat");
            popup.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                if (title.equals("Gallery")) {
                    currentImageSource = "FILE";
                    btnPickImage.setVisibility(View.VISIBLE);
                    editImageUrl.setVisibility(View.GONE);
                } else if (title.equals("Camera")) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
                    currentImageSource = "FILE";
                    btnPickImage.setVisibility(View.GONE);
                    editImageUrl.setVisibility(View.GONE);
                    selectedImageUri = getTmpFileUri();
                    customCameraLauncher.launch(new Intent(this, CameraActivity.class));
                } else {
                    currentImageSource = "API";
                    btnPickImage.setVisibility(View.GONE);
                    editImageUrl.setVisibility(View.VISIBLE);
                    fetchRandomCat(editImageUrl, imgPreview);
                }
                return true;
            });
            popup.show();
        });

        btnPickImage.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

        findViewById(R.id.btnSave).setOnClickListener(v -> processAction(null, editTop, editBottom, editImageUrl));
        
        String targetReceiver = getIntent().getStringExtra("TARGET_RECEIVER");
        findViewById(R.id.btnSendMessage).setOnClickListener(v -> {
            if (targetReceiver != null && !targetReceiver.isEmpty()) {
                processAction(targetReceiver, editTop, editBottom, editImageUrl);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Receiver Name");
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("Send", (dialog, which) -> 
                    processAction(input.getText().toString(), editTop, editBottom, editImageUrl));
                builder.show();
            }
        });
    }

    private void processAction(String receiver, EditText top, EditText bottom, EditText imgUrl) {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sender = prefs.getString("last_logged_in_user", "Guest");
                JSONObject localMemeJson = new JSONObject();
                localMemeJson.put("toptext", top.getText().toString());
                localMemeJson.put("bottomtext", bottom.getText().toString());
                
                String localImgPath = currentImageSource.equals("FILE") && permanentLocalPath != null ? 
                        permanentLocalPath : imgUrl.getText().toString();
                localMemeJson.put("imagedirectory", localImgPath);

                Meme memeObj = new Meme(localMemeJson.toString(), this);
                Message localMsg = new Message(sender, receiver != null ? receiver : "Draft", memeObj);
                AppDatabase.getDb(this).MessageDao().insertMessage(localMsg);
                
                if (receiver != null) {
                    Contact c = new Contact(receiver, sender, null);
                    AppDatabase.getDb(this).ContactDao().addContact(c);
                    JSONObject networkMemeJson = new JSONObject(localMemeJson.toString());
                    if (currentImageSource.equals("FILE") && selectedImageUri != null) {
                        networkMemeJson.put("imagedirectory", convertUriToBase64(selectedImageUri));
                    }
                    JSONObject packageJson = new JSONObject();
                    packageJson.put("sender", sender);
                    packageJson.put("receiver", receiver);
                    packageJson.put("meme", networkMemeJson);
                    URL url = new URL("http://192.168.0.150:5000/api/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) { os.write(packageJson.toString().getBytes("utf-8")); }
                    if (conn.getResponseCode() == 200) runOnUiThread(() -> Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show());
                }
                finish();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
}
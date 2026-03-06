package com.example.messagingapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileActivity extends AppCompatActivity {
    private String selectedImagePath = "";
    private ImageView profilePreview;
    
    private final ActivityResultLauncher<PickVisualMediaRequest> pickPFP =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
                        saveImage(uri);
                    } catch (Exception e){
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        AppDatabase db = AppDatabase.getDb(this);
        
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String lastUserName = prefs.getString("last_logged_in_user", null);
        
        User user = null;
        if (lastUserName != null) {
            user = db.UserDao().getUserByName(lastUserName);
        }
        
        if (user == null){
            user = new User(ProfileActivity.this);
        }

        ImageView backBtn = findViewById(R.id.btnBack);
        profilePreview = findViewById(R.id.profilePreview);
        
        if (user.getProfilepic() != null) {
            profilePreview.setImageURI(Uri.parse(user.getProfilepic()));
            selectedImagePath = user.getProfilepic();
        }

        backBtn.setOnClickListener(v -> finish());

        EditText usernameInput = findViewById(R.id.editCreatorName);
        usernameInput.setText(user.getUsername());

        Button savebutton = findViewById(R.id.btnSaveProfile);
        
        User finalUser = user; 
        
        savebutton.setOnClickListener(v -> {
            String newName = usernameInput.getText().toString().trim();
            
            if (newName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String picToSave = selectedImagePath;
            if (picToSave.isEmpty()) {
                picToSave = finalUser.getProfilepic();
            }

            User newUser = new User(newName, picToSave);
            db.UserDao().save(newUser);

            prefs.edit().putString("last_logged_in_user", newName).apply();

            Toast.makeText(ProfileActivity.this, "Profile Saved as " + newName, Toast.LENGTH_SHORT).show();
            finish();
        });

        Button photopick = findViewById(R.id.btnPickPhoto);
        photopick.setOnClickListener(v -> {
            pickPFP.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    private void saveImage(Uri uri) throws IOException {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            String filename = "pfp_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), filename);
            
            OutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.close();
            is.close();
            
            selectedImagePath = file.getAbsolutePath();
            profilePreview.setImageURI(Uri.fromFile(file));

        } catch (Exception e) {
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
}
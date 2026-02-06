package com.example.messagingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
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
                      throw new RuntimeException("Intentional Crash: " + e.getMessage(), e);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        AppDatabase db = AppDatabase.getDb(this);
        User user = db.UserDao().getUser();
        ImageView backBtn = findViewById(R.id.btnBack);
        if (user == null){
            user = new User(ProfileActivity.this);
        }
        profilePreview = findViewById(R.id.profilePreview);
        profilePreview.setImageURI(Uri.parse(user.getProfilepic()));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        EditText username = findViewById(R.id.editCreatorName);
        username.setHint(user.getUsername());
        Button savebutton = findViewById(R.id.btnSaveProfile);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatabase db = AppDatabase.getDb(ProfileActivity.this);
                User user = db.UserDao().getUser();
                String addusername = username.getText().toString();
                String addpic = selectedImagePath;
                if (addusername.isEmpty()){
                    addusername = user.getUsername();
                };
                if (addpic.isEmpty()){
                    addpic = user.getProfilepic();
                };
                User newuser = new User(addusername,addpic);
                db.UserDao().save(newuser);
                Toast.makeText(ProfileActivity.this, "Profile Saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        Button photopick = findViewById(R.id.btnPickPhoto);
        photopick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPFP.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
    }
    private void saveImage(Uri uri) throws IOException {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile_pic.jpg");
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
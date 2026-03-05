package com.example.messagingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;

public class MemeCreatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_creator);

        // --- UI Elements for Image ---
        Button btnImageSource = findViewById(R.id.btnImageSource);
        Button btnPickImage = findViewById(R.id.btnPickImage);
        EditText editImageUrl = findViewById(R.id.editImageUrl);

        // --- UI Elements for Audio ---
        Button btnAudioSource = findViewById(R.id.btnAudioSource);
        EditText editAudioFile = findViewById(R.id.editAudioName);
        EditText editAudioUrl = findViewById(R.id.editAudioUrl);

        // --- Logic: Image Popup Menu ---
        btnImageSource.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Local File");
            popup.getMenu().add("API URL");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Local File")) {
                    btnImageSource.setText("Image: Local File");
                    btnPickImage.setVisibility(View.VISIBLE);
                    editImageUrl.setVisibility(View.GONE);
                } else {
                    btnImageSource.setText("Image: API URL");
                    btnPickImage.setVisibility(View.GONE);
                    editImageUrl.setVisibility(View.VISIBLE);
                }
                return true;
            });
            popup.show();
        });

        // --- Logic: Audio Popup Menu ---
        btnAudioSource.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Local File");
            popup.getMenu().add("API URL");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Local File")) {
                    btnAudioSource.setText("Audio: Local File");
                    editAudioFile.setVisibility(View.VISIBLE);
                    editAudioUrl.setVisibility(View.GONE);
                } else {
                    btnAudioSource.setText("Audio: API URL");
                    editAudioFile.setVisibility(View.GONE);
                    editAudioUrl.setVisibility(View.VISIBLE);
                }
                return true;
            });
            popup.show();
        });

        // --- Logic: Save & Send ---
        Button btnSend = findViewById(R.id.btnSendMessage);
        btnSend.setOnClickListener(v -> {
            // TODO: Here you will construct your Message object and save to DB
            finish();
        });
    }
}
package com.example.messagingapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import java.io.File;

public class MemeActivity extends AppCompatActivity {
    private Meme meme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String rawJson = getIntent().getStringExtra("RAW_JSON");
        if (rawJson != null) {
            meme = new Meme(rawJson, this);
        }

        if (meme != null) {
            TextView toptext = findViewById(R.id.toptext);
            TextView bottomtext = findViewById(R.id.bottomtext);
            ImageView memeimage = findViewById(R.id.imageView);

            toptext.setText(meme.getToptext());
            bottomtext.setText(meme.getBottomtext());
            
            String imgPath = meme.getImage();
            
            if (imgPath != null) {
                if (imgPath.startsWith("/")) {
                    Glide.with(this).load(new File(imgPath)).into(memeimage);
                } else {
                    Glide.with(this)
                         .load(imgPath)
                         .placeholder(R.drawable.profile_placeholder)
                         .into(memeimage);
                }
            }
        }

        findViewById(R.id.btnBack2).setOnClickListener(v -> finish());
    }
}
package com.example.messagingapp;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.List;

public class MemeActivity extends AppCompatActivity {
    private Meme meme;
    private MediaPlayer mp = null;
    private ScrollView myScroller;
    private List<String> lyrics;
    private final android.os.Handler lyricHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    private void player(TextView txt, Button bt) {
        try {
            if (mp != null) { mp.release(); }
            mp = new MediaPlayer();
            
            String audioSource = meme.getAudioname();
            if (audioSource.startsWith("content://") || audioSource.startsWith("/")) {
                // Play from local file or URI
                mp.setDataSource(this, Uri.parse(audioSource));
            } else {
                // Play from assets (HW1 style)
                AssetFileDescriptor afd = getAssets().openFd("sounds/" + audioSource + ".mp3");
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }
            
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayLyricsSequentially(0, "", txt, bt);
    }

    private void displayLyricsSequentially(int index, String currentFullText, TextView txt, Button bt) {
        if (lyrics != null && index < lyrics.size()) {
            String updatedText = currentFullText + lyrics.get(index) + "\n";
            txt.setText(updatedText);
            myScroller.post(() -> myScroller.fullScroll(View.FOCUS_DOWN));
            lyricHandler.postDelayed(() -> displayLyricsSequentially(index + 1, updatedText, txt, bt), 3000);
        } else {
            bt.setEnabled(true);
        }
    }

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
            lyrics = meme.getSubtitles();
            ((TextView) findViewById(R.id.toptext)).setText(meme.getToptext());
            ((TextView) findViewById(R.id.bottomtext)).setText(meme.getBottomtext());
            
            ImageView memeimage = findViewById(R.id.imageView);
            String imgPath = meme.getImage();
            
            if (imgPath.startsWith("/")) {
                // Load local file saved by Worker
                Glide.with(this).load(new File(imgPath)).into(memeimage);
            } else {
                // Load URL from API
                Glide.with(this).load(imgPath).placeholder(R.drawable.profile_placeholder).into(memeimage);
            }
        }

        myScroller = findViewById(R.id.myScroller);
        findViewById(R.id.btnBack2).setOnClickListener(v -> finish());
        
        Button myButton = findViewById(R.id.button2);
        myButton.setOnClickListener(v -> {
            myButton.setEnabled(false);
            player(findViewById(R.id.textView), myButton);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            if (mp.isPlaying()) mp.stop();
            mp.release();
        }
        lyricHandler.removeCallbacksAndMessages(null);
    }
}
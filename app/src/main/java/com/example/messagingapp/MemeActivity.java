package com.example.messagingapp;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MemeActivity extends AppCompatActivity {
    private Meme meme;
    MediaPlayer mp = null;
    private ScrollView myScroller;
    private List<String> lyrics;
    private final android.os.Handler lyricHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private void player(TextView txt, Button bt) {
        try {
            AssetFileDescriptor afd = getAssets().openFd("sounds/" + meme.getAudioname() + ".mp3");
            if (mp != null) { mp.release(); }
            mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            mp.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        displayLyricsSequentially(0, "", txt, bt);

    }

    private void displayLyricsSequentially(int index, String currentFullText, TextView txt, Button bt) {
        if (index < meme.getSubtitles().size()) {
            String updatedText = currentFullText + lyrics.get(index) + "\n";
            txt.setText(updatedText);
            myScroller.post(new Runnable() {
                @Override
                public void run() {
                    myScroller.fullScroll(View.FOCUS_DOWN);
                }
            });
            lyricHandler.postDelayed(() -> {
                displayLyricsSequentially(index + 1, updatedText, txt, bt);
            }, 3000);
        } else {
            bt.setEnabled(true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String jsonFile = getIntent().getStringExtra("JSON_FILE");
        meme = new Meme(jsonFile, this);
        lyrics = meme.getSubtitles();
        setContentView(R.layout.activity_main);
        Button myButton = findViewById(R.id.button2);
        myButton.setText("Press Me");
        TextView subtitles = findViewById(R.id.textView);
        subtitles.setText("");
        TextView toptext = findViewById(R.id.toptext);
        toptext.setText(meme.getToptext());
        TextView bottomtext = findViewById(R.id.bottomtext);
        bottomtext.setText(meme.getBottomtext());
        ImageView memeimage = findViewById(R.id.imageView);
        memeimage.setImageDrawable(meme.getImage(this));
        myScroller = findViewById(R.id.myScroller);
        Button backBtn = findViewById(R.id.buttonBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButton.setEnabled(false);
                player(subtitles, myButton);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
        lyricHandler.removeCallbacksAndMessages(null);
    }
}
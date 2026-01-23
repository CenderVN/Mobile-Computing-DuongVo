package com.example.messagingapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Integer counter = 0;
    private String lyric = "";
    MediaPlayer mp = null;
    private ScrollView myScroller;
    private List<String> lyrics = new ArrayList<>();

    private void imanewsoul(TextView txt, Button bt) {
        mp = MediaPlayer.create(MainActivity.this, R.raw.imanewsoul);
        mp.start();
        displayLyricsSequentially(0, "", txt, bt);

    }

    private void displayLyricsSequentially(int index, String currentFullText, TextView txt, Button bt) {
        if (index < lyrics.size()) {
            String updatedText = currentFullText + lyrics.get(index) + "\n";
            txt.setText(updatedText);
            myScroller.post(new Runnable() {
                @Override
                public void run() {
                    myScroller.fullScroll(View.FOCUS_DOWN);
                }
            });
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                displayLyricsSequentially(index + 1, updatedText, txt, bt);
            }, 3000);
        } else {
            bt.setEnabled(true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lyrics.add("I'm a new soul");
        lyrics.add("I came to this strange world");
        lyrics.add("Hoping I could learn a bit about how to give and take");
        lyrics.add("But since I came here");
        lyrics.add("Felt the joy and the fear");
        lyrics.add("Finding myself making every possible mistake");
        lyrics.add("La-la-la-la, la-la-la-la-la-la\n" +
                "La-la-la-la-la, la-la-la, la-la-la");

        setContentView(R.layout.activity_main);
        Button myButton = findViewById(R.id.button2);
        myButton.setText("Press Me");
        TextView myHelloText = findViewById(R.id.textView);
        myHelloText.setText("");
        myScroller = findViewById(R.id.myScroller);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButton.setEnabled(false);
                imanewsoul(myHelloText, myButton);
            }
        });
    }
}
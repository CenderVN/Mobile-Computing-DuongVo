package com.example.messagingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        LinearLayout container = findViewById(R.id.menuContainer);
        try {
            String[] files = getAssets().list("jsons");

            if (files != null) {
                for (String filename : files) {
                    if (filename.endsWith(".json")) {
                        Button btn = new Button(this);
                        String displayName = filename.replace(".json", "").toUpperCase();
                        btn.setText(displayName);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 20);
                        btn.setLayoutParams(params);
                        btn.setOnClickListener(v -> {
                            Intent intent = new Intent(MainActivity.this, MemeActivity.class);
                            intent.putExtra("JSON_FILE", filename);
                            startActivity(intent);
                        });
                        container.addView(btn);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
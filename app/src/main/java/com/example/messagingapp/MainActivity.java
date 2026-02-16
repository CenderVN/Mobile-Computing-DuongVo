package com.example.messagingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        updateProfileUI();
    }
    private void addEasterEgg(LinearLayout container) {
        Button eggBtn = new Button(this);
        eggBtn.setText("?? SURPRISE ME ??");
        eggBtn.setBackgroundColor(android.graphics.Color.parseColor("#FFD700"));
        eggBtn.setTextColor(android.graphics.Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 40);
        eggBtn.setLayoutParams(params);

        eggBtn.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    java.net.URL url = new java.net.URL("https://api.thecatapi.com/v1/images/search");
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    java.util.Scanner s = new java.util.Scanner(conn.getInputStream()).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : "";
                
                    org.json.JSONArray array = new org.json.JSONArray(response);
                    String catUrl = array.getJSONObject(0).getString("url");
                    org.json.JSONObject mockJson = new org.json.JSONObject();
                    mockJson.put("toptext", "EASTER EGG");
                    mockJson.put("bottomtext", "FOUND!");
                    mockJson.put("imagedirectory", catUrl); // Passing URL as the directory

                    runOnUiThread(() -> {
                        Intent intent = new Intent(MainActivity.this, MemeActivity.class);
                        intent.putExtra("RAW_JSON", mockJson.toString());
                        startActivity(intent);
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Cat escaped!", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
        container.addView(eggBtn, 0);
    }
    private void updateProfileUI() {
        AppDatabase db = AppDatabase.getDb(this);
        User user = db.UserDao().getUser();
        if (user == null) {
            user = new User(MainActivity.this); 
        }
        TextView title = findViewById(R.id.txtFeedTitle);
        ImageView profileIcon = findViewById(R.id.btnProfile);
        title.setText("Meme Feed - " + user.getUsername());
        profileIcon.setImageURI(null); 
        profileIcon.setImageURI(Uri.parse(user.getProfilepic()));
        
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
        PeriodicWorkRequest memeCheckRequest =
        new PeriodicWorkRequest.Builder(MemeWorker.class, 15, TimeUnit.MINUTES)
                    .setInitialDelay(10, TimeUnit.SECONDS)
                    .build();
                
        WorkManager.getInstance(this).enqueue(memeCheckRequest);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ImageView profilebtn = findViewById(R.id.btnProfile);
        profilebtn.setOnClickListener(v -> {
                            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        });
        LinearLayout container = findViewById(R.id.menuContainer);
        addEasterEgg(container);
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
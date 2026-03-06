package com.example.messagingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private String contactName;
    private String currentUsername;
    private RecyclerView rvMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        contactName = getIntent().getStringExtra("CONTACT_NAME");
        if (contactName == null) contactName = "Friend";
        
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("last_logged_in_user", "Guest");

        TextView nameView = findViewById(R.id.contactName);
        nameView.setText(contactName);
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvMessages = findViewById(R.id.rvMessages);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList, currentUsername);
        rvMessages.setAdapter(adapter);

        loadMessages();

        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            Intent i = new Intent(this, MemeCreatorActivity.class);
            startActivity(i);
        });
        
        findViewById(R.id.btnPremade).setOnClickListener(v -> finish()); 
    }

    private void loadMessages() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDb(this);
            List<Message> msgs = db.MessageDao().getConversation(currentUsername, contactName); 
            
            runOnUiThread(() -> {
                messageList.clear();
                messageList.addAll(msgs);
                adapter.notifyDataSetChanged();
                
                // Auto-scroll to the newest message at the bottom
                if (!messageList.isEmpty()) {
                    rvMessages.scrollToPosition(messageList.size() - 1);
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages(); 
    }
}
package com.example.messagingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        AppDatabase.getDb(this).MessageDao().getConversation(currentUsername, contactName).observe(this, msgs -> {
            if (msgs != null) {
                messageList.clear();
                messageList.addAll(msgs);
                adapter.notifyDataSetChanged();
                
    
                if (!messageList.isEmpty()) {
                    rvMessages.scrollToPosition(messageList.size() - 1);
                }
            }
        });

        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            Intent i = new Intent(this, MemeCreatorActivity.class);
            i.putExtra("TARGET_RECEIVER", contactName);
            startActivity(i);
        });
    }
}
package com.example.messagingapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.room.Ignore;

@Entity(tableName = "messages")
public class Message {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String senderId;
    private String receiverId;  
    
    @TypeConverters(MemeConverter.class)
    private Meme content;
    
    private long timestamp;
    public Message() {}
    @Ignore
    public Message(String senderId, String receiverId, Meme content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public Meme getContent() { return content; }
    public void setContent(Meme content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
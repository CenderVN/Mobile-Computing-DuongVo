package com.example.messagingapp;

import java.io.Serializable;

public class Message implements Serializable {
    private int senderId;
    private int receiverId;
    private Meme content;
    private long timestamp;
    
    public Message() {}
    
    public Message(int senderId, int receiverId, Meme content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public Meme getContent() { return content; }
    public void setContent(Meme content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
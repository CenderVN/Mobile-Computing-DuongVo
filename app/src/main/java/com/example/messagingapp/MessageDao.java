package com.example.messagingapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insertMessage(Message message);
    @Query("SELECT * FROM messages WHERE " +
           "(senderId = :me AND receiverId = :them) OR " +
           "(senderId = :them AND receiverId = :me) " +
           "ORDER BY timestamp ASC")
    List<Message> getConversation(String me, String them);

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    List<Message> getAllMessages();
}
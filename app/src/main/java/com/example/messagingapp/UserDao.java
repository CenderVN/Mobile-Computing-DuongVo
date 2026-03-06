package com.example.messagingapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    
    @Query("SELECT * FROM users LIMIT 1") 
    User getAnyUser();

    
    @Query("SELECT * FROM users WHERE username = :name LIMIT 1")
    User getUserByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(User user);
}
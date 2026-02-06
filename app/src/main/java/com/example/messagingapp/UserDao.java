package com.example.messagingapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User WHERE id = 1")
    User getUser();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(User user);
}
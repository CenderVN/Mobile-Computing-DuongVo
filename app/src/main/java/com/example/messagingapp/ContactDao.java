package com.example.messagingapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addContact(Contact contact);

    @Query("SELECT * FROM contacts WHERE ownerName = :owner ORDER BY name ASC")
    LiveData<List<Contact>>  getContactsByOwner(String owner);

    @Query("SELECT * FROM contacts WHERE name = :name AND ownerName = :owner LIMIT 1")
    Contact getContactByName(String name, String owner);
}
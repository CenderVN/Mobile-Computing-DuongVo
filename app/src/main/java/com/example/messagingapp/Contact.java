package com.example.messagingapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts", primaryKeys = {"name", "ownerName"})
public class Contact {
    @NonNull
    private String name; 
    @NonNull
    private String ownerName; 
    private String profilePicUrl;

    public Contact() {}

    public Contact(@NonNull String name, @NonNull String ownerName, String profilePicUrl) {
        this.name = name;
        this.ownerName = ownerName;
        this.profilePicUrl = profilePicUrl;
    }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(@NonNull String ownerName) { this.ownerName = ownerName; }

    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
}
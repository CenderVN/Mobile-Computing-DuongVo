package com.example.messagingapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull 
    private String username;
    
    private String profilepic;
    public User(@NonNull String username, String profilepic) {
        this.username = username;
        this.profilepic = profilepic;
    }
    public User(Context context) {
        this.username = "Guest";
        this.profilepic = "android.resource://" + context.getPackageName() + "/" + R.drawable.profile_placeholder;
    }

    public User() {
        this.username = "Guest";
        this.profilepic = "";
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }
}
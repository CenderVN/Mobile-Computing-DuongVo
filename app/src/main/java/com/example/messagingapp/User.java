package com.example.messagingapp;
import android.content.Context;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class User {
    @PrimaryKey
    private int id = 1;
    private String username;
    private String profilepic;

    public User(String username, String profilepic) {
        this.username = username;
        this.profilepic = profilepic;
    }
    public User(Context context) {
        this.username = "No Name";
        this.profilepic = "android.resource://" + context.getPackageName() + "/" + R.drawable.profile_placeholder;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilepic() {
        return profilepic;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}


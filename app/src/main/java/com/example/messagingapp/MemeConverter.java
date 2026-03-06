package com.example.messagingapp;

import androidx.room.TypeConverter;
import com.google.gson.Gson;

public class MemeConverter {
    @TypeConverter
    public static String fromMeme(Meme meme) {
        return new Gson().toJson(meme);
    }

    @TypeConverter
    public static Meme toMeme(String json) {
        return new Gson().fromJson(json, Meme.class);
    }
}
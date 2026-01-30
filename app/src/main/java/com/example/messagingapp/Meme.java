package com.example.messagingapp;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Meme {
    private String toptext = "";
    private String bottomtext = "";
    private String imagedirectory = "";
    private List<String> subtitles = new ArrayList<>();
    private String audioname = "";

    public Meme(String jsonFilePath, Context context) {
        String jsonString = filereader(jsonFilePath, context);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            this.toptext = jsonObject.optString("toptext", "");
            this.bottomtext = jsonObject.optString("bottomtext", "");
            this.imagedirectory = jsonObject.optString("imagedirectory", "");
            this.audioname = jsonObject.optString("audioname", "");
            JSONArray jsonArray = jsonObject.optJSONArray("subtitles");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    this.subtitles.add(jsonArray.getString(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String filereader(String jsonFilePath,Context context){
        try {
            InputStream is = context.getAssets().open("jsons/"+jsonFilePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getToptext() { return toptext; }
    public String getBottomtext() { return bottomtext; }
    public String getImagedirectory() { return imagedirectory; }
    public List<String> getSubtitles() { return subtitles; }
    public String getAudioname(){return audioname;}
    public Drawable getImage(Context context) {
        try {
            InputStream is = context.getAssets().open("images/" + imagedirectory);
            Drawable d = Drawable.createFromStream(is, null);
            is.close();
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if file not found
        }
    }
}
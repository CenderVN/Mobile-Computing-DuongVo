package com.example.messagingapp;

import android.content.Context;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Meme {
    private String toptext = "";
    private String bottomtext = "";
    private String imagedirectory = "";

    public Meme() {}
    
    public Meme(String input, Context context) {
        String jsonString;
        if (input != null && input.trim().startsWith("{")) {
            jsonString = input;
        } else {
            jsonString = filereader(input, context);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            this.toptext = jsonObject.optString("toptext", "");
            this.bottomtext = jsonObject.optString("bottomtext", "");
            this.imagedirectory = jsonObject.optString("imagedirectory", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String filereader(String jsonFilePath, Context context){
        try {
            InputStream is = context.getAssets().open("jsons/"+jsonFilePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public String getToptext() { return toptext; }
    public String getBottomtext() { return bottomtext; }
    public String getImage() { return imagedirectory; }
}
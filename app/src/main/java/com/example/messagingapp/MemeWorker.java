package com.example.messagingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemeWorker extends Worker {
    private static final String SERVER_URL = "http://192.168.0.150:5000/get-meme";

    public MemeWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        HttpURLConnection conn = null;
        try {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String currentUser = prefs.getString("last_logged_in_user", "Guest");

            URL url = new URL(SERVER_URL + "?user=" + currentUser);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);

                JSONArray messagesArray = new JSONArray(result.toString());

                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject packageJson = messagesArray.getJSONObject(i);
                    String sender = packageJson.optString("sender", "System");
                    JSONObject memeJson = packageJson.getJSONObject("meme");

                    String imgData = memeJson.optString("imagedirectory", "");
                    if (imgData.length() > 500 && !imgData.startsWith("http")) {
                        try {
                            byte[] decoded = Base64.decode(imgData, Base64.DEFAULT);
                            File file = new File(getApplicationContext().getFilesDir(), "rec_" + System.currentTimeMillis() + "_" + i + ".jpg");
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(decoded);
                            fos.close();
                            memeJson.put("imagedirectory", file.getAbsolutePath());
                        } catch (Exception e) {
                            Log.e("MemeWorker", "Image error: " + e.getMessage());
                        }
                    }

                    String memeDataString = memeJson.toString();
                    Meme receivedMeme = new Meme(memeDataString, getApplicationContext());
                    Message msg = new Message(sender, currentUser, receivedMeme);
                    
                    AppDatabase db = AppDatabase.getDb(getApplicationContext());
                    db.MessageDao().insertMessage(msg);

                    Contact senderContact = new Contact(sender, currentUser, null);
                    db.ContactDao().addContact(senderContact);

                    NotificationHelper.showMemeNotification(getApplicationContext(), memeDataString);
                }
                return Result.success();
            }
        } catch (Exception e) {
            Log.e("MemeWorker", "Work failed: " + e.getMessage());
            return Result.failure();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return Result.retry();
    }
}
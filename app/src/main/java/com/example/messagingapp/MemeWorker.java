package com.example.messagingapp;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
public class MemeWorker extends Worker {
    private static final String SERVER_URL = "http://192.168.0.150:5000/get-meme";
    public MemeWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) { result.append(line); }
                NotificationHelper.showMemeNotification(getApplicationContext(), result.toString());
                return Result.success();
            } else if (responseCode == 403) {
                return Result.retry();
            } 
        } catch (Exception e) {
            Log.e("MemeWorker", "Error fetching meme: " + e.getMessage());
            return Result.failure();
        } finally {
            if (connection != null) connection.disconnect();
        }
        return Result.success();
    }
}
package com.example.messagingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;
import androidx.work.OneTimeWorkRequest;
import androidx.core.splashscreen.SplashScreen;
import androidx.work.ExistingWorkPolicy;

public class MainActivity extends AppCompatActivity {
    private String currentUsername;

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileUI();
    }

    private void updateProfileUI() {
        AppDatabase db = AppDatabase.getDb(this);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("last_logged_in_user", "Guest");

        User user = db.UserDao().getUserByName(currentUsername);
        if (user == null) user = new User(this);

        TextView title = findViewById(R.id.txtFeedTitle);
        ImageView profileIcon = findViewById(R.id.btnProfile);
        title.setText("Meme Feed - " + user.getUsername());
        
        if (user.getProfilepic() != null) {
            profileIcon.setImageURI(null);
            profileIcon.setImageURI(Uri.parse(user.getProfilepic()));
        }
    }

    private void addContactView(LinearLayout container, Contact contact) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(20, 20, 20, 20);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setClickable(true);
        row.setFocusable(true);
        row.setBackgroundResource(android.R.drawable.list_selector_background);

        ShapeableImageView iv = new ShapeableImageView(this);
        iv.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setShapeAppearanceModel(iv.getShapeAppearanceModel().toBuilder()
                .setAllCornerSizes(60f).build());
        if (contact.getProfilePicUrl() != null) {
            iv.setImageURI(Uri.parse(contact.getProfilePicUrl()));
        }

        TextView tv = new TextView(this);
        tv.setText(contact.getName());
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(20);
        tv.setPadding(30, 0, 0, 0);

        row.addView(iv);
        row.addView(tv);
        row.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("CONTACT_NAME", contact.getName());
            startActivity(intent);
        });

        container.addView(row);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
        
        OneTimeWorkRequest initialCheck = new OneTimeWorkRequest.Builder(MemeWorker.class).build();
        WorkManager.getInstance(this).enqueueUniqueWork("MemeSync", ExistingWorkPolicy.KEEP, initialCheck);
        
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        AppDatabase db = AppDatabase.getDb(this);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("last_logged_in_user", "Guest");

        LinearLayout container = findViewById(R.id.menuContainer);
        
        db.ContactDao().getContactsByOwner(currentUsername).observe(this, contacts -> {
            container.removeAllViews();
            if (contacts != null) {
                for (Contact contact : contacts) {
                    addContactView(container, contact);
                }
            }
        });
        
        findViewById(R.id.btnCreateMeme).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MemeCreatorActivity.class));
        });

        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }
}
package com.example.messagingapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class, Message.class, Contact.class}, version = 4, exportSchema = false)
@TypeConverters({MemeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static AppDatabase instance;
    
    public abstract UserDao UserDao();
    public abstract MessageDao MessageDao();
    public abstract ContactDao ContactDao();

    public static synchronized AppDatabase getDb(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "User_db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
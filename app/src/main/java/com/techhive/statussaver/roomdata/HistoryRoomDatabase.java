package com.techhive.statussaver.roomdata;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.techhive.statussaver.model.History;

@Database(entities = {History.class}, version = 4, exportSchema = false)
public abstract class HistoryRoomDatabase extends RoomDatabase {
    private static final String LOG_TAG = HistoryRoomDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "historyList";
    private static HistoryRoomDatabase sInstance;

    public static HistoryRoomDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                HistoryRoomDatabase.class, HistoryRoomDatabase.DATABASE_NAME).allowMainThreadQueries().enableMultiInstanceInvalidation()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract HistoryDao historyDao();
}

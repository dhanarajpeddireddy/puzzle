package com.dana.puzzle.database;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.room.Room;

import com.dana.puzzle.BuildConfig;

public class DatabaseClient {

    @SuppressLint("StaticFieldLeak")
    private static DatabaseClient mInstance;

    //our app database object
    private final GameDatabase gameDatabase;

    private DatabaseClient(Context mCtx) {


        //creating the app database with Room database builder
        //MyToDos is the name of the database
        gameDatabase = Room.databaseBuilder(mCtx, GameDatabase.class, BuildConfig.APPLICATION_ID)
                .fallbackToDestructiveMigration().build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public GameDatabase getAppDatabase() {
        return gameDatabase;
    }
}
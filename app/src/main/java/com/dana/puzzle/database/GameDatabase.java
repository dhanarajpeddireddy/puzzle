package com.dana.puzzle.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GameBeen.class}, version = 1)
public abstract class GameDatabase extends RoomDatabase {
    public abstract GameDao gameDao();
}
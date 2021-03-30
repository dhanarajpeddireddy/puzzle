package com.dana.puzzle.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GameDao {

    @Query("SELECT * FROM game")
    List<GameBeen> getAll();

    @Insert
    void insert(GameBeen gameBeen);

    @Delete
    void delete(GameBeen gameBeen);

    @Update
    void update(GameBeen gameBeen);

}
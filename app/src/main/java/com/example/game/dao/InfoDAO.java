package com.example.game.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.game.model.Info;

import java.util.ArrayList;

@Dao
public interface InfoDAO {
    @Insert
    void insert(Info info);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<Info> info);

    @Update
    void update(Info info);

    @Query("SELECT * FROM info WHERE id = :id")
    Info getInfo(int id);

    @Delete
    void delete(Info info);
}

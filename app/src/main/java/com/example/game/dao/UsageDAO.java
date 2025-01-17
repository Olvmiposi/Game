package com.example.game.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.game.model.Usage;

import java.util.ArrayList;

@Dao
public interface UsageDAO {
    @Insert
    void insert(Usage Usage);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<Usage> Usage);

    @Update
    void update(Usage usage);

    @Query("SELECT * FROM usage WHERE id = :id")
    Usage getUsage(int id);


    @Delete
    void delete(Usage usage);
    @Query("DELETE FROM usage")

    void nukeTable();
}

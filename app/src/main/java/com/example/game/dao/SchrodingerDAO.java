package com.example.game.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.game.model.Schrodinger;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SchrodingerDAO {
    @Insert
    void insert(Schrodinger schrodinger);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<Schrodinger> schrodingers);
    @Update
    void update(Schrodinger schrodinger);
    @Query("SELECT * from schrodinger")
    List<Schrodinger> getSchrodingers();
    @Query("SELECT * FROM schrodinger WHERE id = :id")
    Schrodinger getSchrodingerById(int id);
    @Delete
    void delete(Schrodinger schrodinger);
}

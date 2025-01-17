package com.example.game.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.game.model.SearchString;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SearchStringDAO {
    @Insert
    void insert(SearchString searchString);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<SearchString> searchString);
    @Update
    void update(SearchString searchString);

    @Query("UPDATE strings SET date =:date WHERE username = :username")
    void updateUsername(String date, String username);
    @Query("SELECT * from strings ORDER By date Asc")
    List<SearchString> getSearchString();
    @Query("SELECT * FROM strings WHERE username = :username")
    SearchString getSearchStringByUsername(String username);
    @Delete
    void delete(SearchString searchString);

    @Query("DELETE FROM strings")
    void nukeTable();
}

package com.example.game.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.game.model.ClubStats;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ClubStatsDAO {
    @Insert
    void insert(ClubStats table);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<ClubStats> table);
    @Update
    void update(ClubStats table);
    @Query("SELECT distinct * FROM clubStats WHERE leagueId = :leagueId and season = :season ORDER By position, goalsDiff, datetime desc")
    List<ClubStats> getTable(int leagueId, int season);

    @Query("SELECT distinct * FROM clubStats WHERE leagueId = :leagueId and season = :season and dateTime = :date ORDER By position, goalsFor asc")
    List<ClubStats> getTableByDate(int leagueId, int season, String date);

    @Query("SELECT * FROM clubStats WHERE leagueId = :leagueId and season = :season and dateTime = :date and name = :name ")
    ClubStats getGamePosition(int leagueId, int season, String date, String name);
    @Delete
    void delete(ClubStats table);

    @Query("DELETE FROM clubStats")
    void nukeTable();
}

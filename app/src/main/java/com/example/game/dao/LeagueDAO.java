package com.example.game.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.game.model.League;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface LeagueDAO {
    @Insert
    void insert(League league);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<League> league);
    @Update
    void update(League league);
    @Query("SELECT * from leagues ORDER By leagues.`end` Desc")
    List<League> getLeagues();

    @Query("SELECT * from leagues where leagueId = :leagueId ORDER By leagues.`end` Desc")
    List<League> getLeaguesById(int leagueId);
    @Query("SELECT * FROM leagues WHERE leagueId = :id")
    League getLeagueById(int id);
    @Query("SELECT * FROM leagues WHERE leagueId = :id and season = :season")
    League getLeagueByIdAndSeason(int id, int season);
    @Delete
    void delete(League league);
}

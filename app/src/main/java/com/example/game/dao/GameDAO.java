package com.example.game.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.game.model.Game;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface GameDAO {
    @Insert
    void insert(Game game);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<Game> games);
    @Update
    void update(Game game);
    @Query("SELECT * from games where checked = 1 and leagueId = :leagueId ORDER By leagueId, date, time DESC")
    List<Game> getCheckedGamesByLatestDate(int leagueId);
    @Query("SELECT * from games where checked = 1 and date = :date and leagueId = :leagueId ")
    LiveData<List<Game>> getCheckedGamesByDate(String date, int leagueId);
    @Query("SELECT * from games where checked = 1 ")
    LiveData<List<Game>> getAllCheckedGames();
    @Query("SELECT * from games where checked = 1 and home = :home")
    List<Game> getCheckedGamesByHome(String home);
    @Query("SELECT * from games where checked = 1 and away = :away")
    List<Game> getCheckedGamesByAway(String away);

    @Query("SELECT * from games where away = :club or home = :club and checked = 1")
    List<Game> getCheckedGamesByClub(String club);
    @Query("SELECT * from games where checked = 1 and season = :season ")
    List<Game> getAllCheckedGamesBySeason(int season);
    @Query("SELECT * from games where checked = 1 and leagueId = :leagueId")
    LiveData<List<Game>> getAllCheckedGamesByLeagueId(int leagueId);
    @Query("SELECT * from games where  score1 = '0' and score2 = '0' and checked = 0  ORDER By date Asc")
    LiveData<List<Game>> getGames();
    @Query("SELECT * from games where  score1 = '0' and score2 = '0' and checked = 0  ORDER By date Asc")
    List<Game> getGamesArrayList();
    @Query("SELECT * from games where score1 = '0' and score2 = '0' and checked = 0  ORDER By date Asc")
    List<Game> getGamesLeagueId();
    // get the list of games in the leagues which has 0 - 0
    @Query("SELECT * from games where  score1 = '0' and score2 = '0' and checked = 0 and leagueId = :id  ORDER By date Asc")
    List<Game> getGamesByLeagueId(int id);

    @Query("SELECT * from games where checked = 1 and leagueId = :id ")
    LiveData<List<Game>> getPasswordsByLeagueId(int id);

    @Query("SELECT * from games where checked = 0 and fixtureId = :id  ORDER By date Asc")
    LiveData<List<Game>> getGamesByFixtureId(int id);

    @Query("SELECT * from games where score1 = '0' and score2 = '0' and checked = 0  and date = :date  ORDER By date, time Asc")
    LiveData<List<Game>> getAllGamesPlayingToday(String date);

    @Query("SELECT * from games where schrodinger = 1 ORDER By leagueId, date, time Asc")
    LiveData<List<Game>> getSchrodingerGames();

    @Query("SELECT * from games where schrodinger = 1 ORDER By leagueId, date, time Asc")
    List<Game> getSchrodingerGamesList();

    @Query("SELECT * from games where schrodinger = 1 and leagueId = :leagueID ORDER By leagueId, date, time Asc")
    List<Game> getSchrodingerGamesListByLeagueId(int leagueID);


    @Query("SELECT * from games where date = :date  ORDER By date Asc")
    LiveData<List<Game>> getAllGames(String date);

    @Query("SELECT * from games where fixtureId = :fixtureId and checked = 0 ORDER By date Asc")
    LiveData<List<Game>> getGamePossibilities(int fixtureId);

    @Query("SELECT * from games where fixtureId = :fixtureId and checked = 0 ORDER By date Asc")
    List<Game> getGamePossibilitiess(int fixtureId);
    @Query("SELECT * FROM games WHERE id = :id")
    Game getGame(int id);
    @Delete
    void deleteAll(ArrayList<Game> games);
    @Delete
    void delete(Game game);


}

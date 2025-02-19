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
import java.util.Date;
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
    @Query("SELECT * from games where checked = 1 and leagueId = :leagueId and season = :season ORDER By leagueId, date, time DESC")
    List<Game> getCheckedGamesByLatestDate2(int leagueId, int season);
    @Query("SELECT * from games where checked = 1 and date = :date and leagueId = :leagueId ")
    LiveData<List<Game>> getCheckedGamesByDate(String date, int leagueId);

    @Query("SELECT * from games where schrodinger = 1 and date = :date and leagueId = :leageId")
    LiveData<List<Game>> getSchrodingerGamesByDate(String date, int leageId);

    @Query("SELECT * from games where checked = 1 ")
    LiveData<List<Game>> getAllCheckedGames();

    @Query("SELECT * from games where checked = 1 and username LIKE :username and leagueId = :leagueId")
    List<Game> getAllCheckedGames(String username, int leagueId);
    @Query("SELECT * from games where checked = 1 ")
    List<Game> getAllCheckedGamesList();
    @Query("SELECT * from games where checked = 1 and home = :home")
    List<Game> getCheckedGamesByHome1(String home);
    @Query("SELECT * from games where checked = 1 and away = :away")
    List<Game> getCheckedGamesByAway1(String away);
    @Query("SELECT * from games where checked = 1 and home = :home and season = :season")
    List<Game> getCheckedGamesByHome(String home, int season);
    @Query("SELECT * from games where checked = 1 and away = :away and season = :season")
    List<Game> getCheckedGamesByAway(String away, int season);
    @Query("SELECT * from games where checked = 1 and home = :home and season = :season")
    List<Game> getCheckedGamesByHomeSeason(String home, int season);
    @Query("SELECT * from games where checked = 1 and away = :away and season = :season")
    List<Game> getCheckedGamesByAwaySeason(String away, int season);
    @Query("SELECT DISTINCT leagueId FROM games ORDER By date Asc")
    List<Integer> getGameLeagues();
    @Query("SELECT * from games where away = :club or home = :club and checked = 1")
    List<Game> getCheckedGamesByClub(String club);
    @Query("SELECT * from games where  home = :club and checked = 1 and leagueId = :leagueId and season = :season")
    List<Game> getCheckedGamesByClubAndSeasonHome(String club, int leagueId, int season);

    @Query("SELECT * from games where away = :club  and checked = 1 and leagueId = :leagueId and season = :season")
    List<Game> getCheckedGamesByClubAndSeasonAway(String club, int leagueId, int season);
    @Query("SELECT * from games where checked = 1 and season = :season ")
    List<Game> getAllCheckedGamesBySeason(int season);

    @Query("SELECT DISTINCT leagueId from games where season = :season ")
    List<Integer> getDistinctLeagueBySeason(int season);

    @Query("SELECT DISTINCT leagueId from games")
    List<Integer> getDistinctLeague();

    @Query("SELECT * from games  where score1 = '0' and score2 = '0' and season = :season ")
    List<Game> getAllGamesBySeason(int season);
    @Query("SELECT * from games where schrodinger = 1 and season = :season ")
    List<Game> getAllSchrodingerBySeason(int season);
    @Query("SELECT * from games where checked = 1 and leagueId = :leagueId and season = :season")
    LiveData<List<Game>> getAllCheckedGamesByLeagueId(int leagueId, int season);
    @Query("SELECT * from games where  score1 = '0' and score2 = '0' and checked = 0  ORDER By date Asc")
    LiveData<List<Game>> getGames();
    @Query("SELECT * from games where  score1 = '0' and score2 = '0' and checked = 0 and CAST(date AS Date) >= Date('now')  ORDER By date Asc")
    LiveData<List<Game>> getGamesByDate();
    @Query("SELECT * from games")
    List<Game> getGamesArrayList();

    @Query("SELECT DISTINCT home from games where leagueId = :leagueId and season = :season ")
    List<String> getDistinctHomeClub( int  leagueId, int season);

    @Query("SELECT DISTINCT away from games where leagueId = :leagueId and season = :season")
    List<String> getDistinctAwayClub(int  leagueId, int season);

    @Query("SELECT * FROM games\n" +
            "    ORDER BY ABS( strftime( \"%s\", date ) - strftime( \"%s\", date('now') ) ) ASC")
    Game getLatestDate();

//    @Query("select * from (SELECT DISTINCT home from games) h join (SELECT DISTINCT away from games) a ON h.home != a.away ")
//    List<Game> getDistinctClub();

    @Query("SELECT * from games where username LIKE :strings ")
    List<Game> searchUsernames(String strings);

    @Query("SELECT * from games where score1 = '0' and score2 = '0' and checked = 0  ORDER By date Asc")
    List<Game> getGamesLeagueId();
    // get the list of games in the leagues which has 0 - 0
    @Query("SELECT * from games where  score1 = '0' and score2 = '0' and checked = 0 and leagueId = :id  ORDER By date Asc")
    List<Game> getGamesByLeagueId(int id);

    @Query("SELECT * from games where  score1 = '0' and score2 = '0' and checked = 0 and leagueId = :id and date = :latestDate  ORDER By date Asc")
    List<Game> getGamesByLeagueIdAndDate(int id, String latestDate);

    @Query("SELECT * from games where  score1 = '0' and score2 = '0'  and leagueId = :id and season = :season  ORDER By date Asc")
    List<Game> getGamesByLeagueIdAndSeason(int id, int season);
    @Query("SELECT * from games where  schrodinger = 1 and checked = 0 and leagueId = :id and season = :season  ORDER By date Asc")
    List<Game> getSchrodingerByLeagueIdAndSeason(int id, int season);


    @Query("SELECT * from games where checked = 1 and leagueId = :id ")
    LiveData<List<Game>> getPasswordsByLeagueId(int id);

    @Query("SELECT * from games where checked = 0 and fixtureId = :id  ORDER By date Asc")
    LiveData<List<Game>> getGamesByFixtureId(int id);

    @Query("SELECT * from games where score1 = '0' and score2 = '0' and date = :date  ORDER By date, time Asc")
    LiveData<List<Game>> getAllGamesPlayingToday(String date);
    @Query("SELECT * from games where score1 = '0' and score2 = '0' and checked = 0  and date = :date  ORDER By date, time Asc")
    List<Game> getAllGamePlayingToday(String date);

    @Query("SELECT * from games where schrodinger = 1 ORDER By leagueId, date, time Asc")
    LiveData<List<Game>> getSchrodingerGames();

    @Query("SELECT * from games where schrodinger = 1 ORDER By leagueId, date, time Asc")
    List<Game> getSchrodingerGamesList();

    @Query("SELECT * from games where schrodinger = 1 and leagueId = :leagueID ORDER By leagueId, date, time Asc")
    List<Game> getSchrodingerGamesListByLeagueId(int leagueID);

    @Query("SELECT * from games where schrodinger = 1 and leagueId = :leagueID ORDER By leagueId, date, time Asc")
    LiveData<List<Game>> getldSchrodingerGamesListByLeagueId(int leagueID);


    @Query("SELECT * from games where date = :date  ORDER By date Asc")
    LiveData<List<Game>> getAllGames(String date);

    @Query("SELECT * from games where fixtureId = :fixtureId ORDER By date Asc")
    LiveData<List<Game>> getGamePossibilities(int fixtureId);

    @Query("SELECT * from games where fixtureId = :fixtureId and checked = 0 ORDER By date Asc")
    List<Game> getGamePossibilitiess(int fixtureId);
    @Query("SELECT * FROM games WHERE id = :id")
    Game getGame(int id);
    @Query("SELECT * FROM games WHERE username_id = :id")
    Game getGameByUsernameId(int id);
    @Delete
    void deleteAll(ArrayList<Game> games);
    @Delete
    void delete(Game game);

    @Query("DELETE FROM games")
    void nukeTable();
}

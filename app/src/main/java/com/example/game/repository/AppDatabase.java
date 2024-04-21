package com.example.game.repository;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.game.dao.ClubStatsDAO;
import com.example.game.dao.GameDAO;
import com.example.game.dao.InfoDAO;
import com.example.game.dao.LeagueDAO;
import com.example.game.dao.SchrodingerDAO;
import com.example.game.dao.SearchStringDAO;
import com.example.game.dao.UsageDAO;
import com.example.game.dao.UserDAO;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.model.Info;
import com.example.game.model.League;
import com.example.game.model.Schrodinger;
import com.example.game.model.SearchString;
import com.example.game.model.Usage;
import com.example.game.model.User;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Database(entities = {
        User.class, Game.class, League.class, Usage.class, Info.class, SearchString.class,  Schrodinger.class, ClubStats.class},
        version = 2, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase implements Serializable {
    private static final String DB_NAME = "app_db";
    private static AppDatabase appDatabase;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private LiveData<List<Game>> game;
    private Game gamee;
    private List<Game> game1, game2, filteredGame1, filteredGame2,filteredGameHome, filteredGameAway;
    private ArrayList<Game> gameArrayList;
    private List<SearchString> searchStrings;
    private ArrayList<Schrodinger> schrodingers;
    private Schrodinger schrodinger;
    private SearchString searchString;
    private LiveData<List<League>> league;
    private LiveData<Usage> usage;
    private LiveData<Info> info;
    private ClubStats table;
    private ArrayList<ClubStats> standing;
    private ClubStats position;
    private ArrayList<Integer> homeScore, awayScore, maxScore, minScore, mostScore;
    private ArrayList<Game>  homeGame, awayGame;
    private String stringMostHome, stringMostAway;
    private ArrayList<String> stringMostScore, mostOccurred;
    private int maxHome, minHome, maxAway, minAway, mostHome, mostAway;

    public static AppDatabase getAppDb(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }

    /**
     * Override the onCreate method to populate the database.
     * For this sample, we clear the database every time it is created.
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate( SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                appDatabase.userDao();
                appDatabase.gameDao();
                appDatabase.leagueDao();
                appDatabase.usageDao();
                appDatabase.infoDao();
                appDatabase.schrodingerDao();
                appDatabase.clubStatsDao();
                appDatabase.searchStringDao();
            });
        }
    };
    public abstract UserDAO userDao();
    public abstract GameDAO gameDao();
    public abstract LeagueDAO leagueDao();
    public abstract UsageDAO usageDao();
    public abstract InfoDAO infoDao();
    public abstract ClubStatsDAO clubStatsDao();

    public abstract SearchStringDAO searchStringDao();
    public abstract SchrodingerDAO schrodingerDao();

    public void addSchrodinger(final Schrodinger schrodinger) {
        new Thread(() -> {
            try {
                schrodingerDao().insert(schrodinger);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addAllSchrodinger(final ArrayList<Schrodinger> schrodingers) {
        new Thread(() -> {
            try {
                schrodingerDao().insertAll(schrodingers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public ArrayList<Game> getSchrodingersGames() {
        Thread thread = new Thread(() -> {
            try {
                gameArrayList = new ArrayList<>();

                schrodingers = (ArrayList<Schrodinger>) schrodingerDao().getSchrodingers();
                for (Schrodinger schrodinger: schrodingers) {
                    gamee = appDatabase.getGame(schrodinger.getId());
                    gameArrayList.add(gamee);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameArrayList;
    }

    public ArrayList<Schrodinger> getSchrodingers() {
        Thread thread = new Thread(() -> {
            try {
                schrodingers = (ArrayList<Schrodinger>) schrodingerDao().getSchrodingers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schrodingers;
    }

    public void addSearchString(final SearchString search) {
        new Thread(() -> {
            try {
                searchString = searchStringDao().getSearchStringByUsername(search.getUsername());
                if(searchString != null){
                    searchStringDao().updateUsername(search.getDate(), search.getUsername());
                }else{
                    searchStringDao().insert(search);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public ArrayList<SearchString> getSearchString() {
        Thread thread = new Thread(() -> {
            try {
                searchStrings =  searchStringDao().getSearchString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (ArrayList<SearchString>) searchStrings;
    }

    public SearchString getSearchStringByUsername(String username) {
        Thread thread = new Thread(() -> {
            try {
                searchString =  searchStringDao().getSearchStringByUsername(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  searchString;
    }

    public void addToTable(final ClubStats table) {
        new Thread(() -> {
            try {
                clubStatsDao().insert(table);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addAllToTable( ArrayList<ClubStats> table) {
        new Thread(() -> {
            try {
                clubStatsDao().insertAll(table);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public ArrayList<ClubStats> getTable(int id) {
        Thread thread = new Thread(() -> {
            try {
                standing = (ArrayList<ClubStats>) clubStatsDao().getTable(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return standing;
    }
    public void removeTable(final ClubStats table) {
        new Thread(() -> {
            try {
                clubStatsDao().delete(table);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public ArrayList<ClubStats> getTableByDate(int id, String date) {
        Thread thread = new Thread(() -> {
            try {
                standing = (ArrayList<ClubStats>) clubStatsDao().getTableByDate(id, date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return standing;
    }

    public ClubStats getGamePosition(int id, String date, String club) {
        Thread thread = new Thread(() -> {
            try {
                position =  clubStatsDao().getGamePosition(id, date, club);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    public void addUser(final User user) {
        new Thread(() -> {
            try {
                userDao().insert(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void addUserList(final ArrayList<User> user) {
        new Thread(() -> {
            try {
                userDao().insertAll(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void updateUser(final User user) {
        new Thread(() -> {
            try {
                userDao().update(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public User getUser(final int id) {
        final User[] finalUser = new User[1];
        Thread thread = new Thread(() -> {
            try {
                finalUser[0] = userDao().getUser(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalUser[0];
    }
    public User getLoggedInUser() {
        final User[] finalUser = new User[1];
        Thread thread = new Thread(() -> {
            try {
                finalUser[0] = userDao().getLoggedInUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalUser[0];
    }
    public void removeUser(final User user) {
        new Thread(() -> {
            try {
                userDao().delete(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void addGame(final Game game) {
        Thread thread = new Thread(() -> {
            try {
                gameDao().insert(game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addGameList(final ArrayList<Game> game) {
        new Thread(() -> {
            try {
                gameDao().insertAll(game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void updateGame(final Game game) {
        Thread thread = new Thread(() -> {
            try {
                gameDao().update(game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Game> getGamesArrayList() {
        Thread thread = new Thread(() -> {
            try {
                gameArrayList = (ArrayList<Game>) gameDao().getGamesArrayList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameArrayList;
    }
    public LiveData<List<Game>> getGames() {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getGames();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }

    public List<Game> getGamesLeagueId() {
        Thread thread = new Thread(() -> {
            try {
                game1 = gameDao().getGamesLeagueId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game1;

    }

    public ArrayList<Game> getGamesByLeagueId(int id) {
        Thread thread = new Thread(() -> {
            try {
                gameArrayList = (ArrayList<Game>) gameDao().getGamesByLeagueId(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameArrayList;
    }

    public LiveData<List<Game>> getPasswordsByLeagueId(int id) {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getPasswordsByLeagueId(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }

    public LiveData<List<Game>> getGamesByFixtureId(int id) {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getGamesByFixtureId(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }


    public LiveData<List<Game>> getSchrodingerGames() {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getSchrodingerGames();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }

    public List<Game> getSchrodingerGamesList(int LeagueId) {
        Thread thread = new Thread(() -> {
            try {
                game1 = gameDao().getSchrodingerGamesListByLeagueId(LeagueId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game1;
    }

    public List<Game> getSchrodingerGamesListByLeagueId(int LeagueId) {

        ArrayList<Game> newGame = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                gameArrayList = getSchrodingersGames();
                for(Game game : gameArrayList){
                    if (game.getLeagueId() == LeagueId){
                        newGame.add(game);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newGame;
    }



    public List<Game> getCheckedGamesByLatestDate(int leagueId) {

        Thread thread = new Thread(() -> {
            try {
                game1 = gameDao().getCheckedGamesByLatestDate(leagueId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return game1;
    }

    public LiveData<List<Game>> getCheckedGamesByDate(String date, int leagueId) {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getCheckedGamesByDate(date, leagueId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }
    public LiveData<List<Game>> getTodayGame(String date) {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getAllGamesPlayingToday(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;

    }

    public LiveData<List<Game>> getAllCheckedGames() {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getAllCheckedGames();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }

    public ArrayList<Integer> getCheckedGamesByHomeAndAway(String home, String away) {
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        maxScore = new ArrayList<>();

        Thread thread = new Thread(() -> {
            try {
                game1 = gameDao().getCheckedGamesByHome(home);
                game2 = gameDao().getCheckedGamesByAway(away);

                for (Game game : Objects.requireNonNull(game1)){
                    homeScore.add(Integer.parseInt(game.getScore1()));
                }

                for (Game game : Objects.requireNonNull(game2)){
                    awayScore.add(Integer.parseInt(game.getScore2()));
                }
                maxHome = Collections.max(homeScore);
                maxAway = Collections.max(awayScore);

                maxScore.add(0,maxHome);
                maxScore.add(1,maxAway);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxScore;
    }

    public ArrayList<Integer> getCheckedGamesByHomeAndAwayLastFive(String home, String away) {
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        maxScore = new ArrayList<>();

        filteredGame1 = new ArrayList<>();
        filteredGame2 = new ArrayList<>();

        game1 = new ArrayList<>();
        game2 = new ArrayList<>();

        filteredGameHome = new ArrayList<>();
        filteredGameAway = new ArrayList<>();

        // sort all games according to date and put them in arraylist called game1 and game2

        game1.addAll(gameDao().getCheckedGamesByHome(home));
        game1.addAll(gameDao().getCheckedGamesByAway(home));

        game2.addAll(gameDao().getCheckedGamesByHome(away));
        game2.addAll(gameDao().getCheckedGamesByAway(away));

        game1.size();
        game2.size();


        filteredGame1 = sort((ArrayList<Game>) game1);
        filteredGame2 = sort((ArrayList<Game>) game2);

        //get first 5 element of the sorted game and put them in a list called filtered list

        if (filteredGame1.size() >= 5){
            for(int i = 0; i < 5; i++) {
                filteredGameHome.add(filteredGame1.get(i));
            }
        }else{
            filteredGameHome.addAll(filteredGame1);
        }

        if (filteredGame2.size() >= 5){
            for(int i = 0; i < 5; i++) {
                filteredGameAway.add(filteredGame2.get(i));
            }
        }else{
            filteredGameAway.addAll(filteredGame2);
        }


        //put all their scores into arraylist called homescore and awayscore
        for (Game game : Objects.requireNonNull(filteredGameHome)){
            if (Objects.equals(home, game.getHome())){
                homeScore.add(Integer.parseInt(game.getScore1()));
            }else{
                homeScore.add(Integer.parseInt(game.getScore2()));
            }

        }

        for (Game game : Objects.requireNonNull(filteredGameAway)){
            if (Objects.equals(away, game.getAway())){
                awayScore.add(Integer.parseInt(game.getScore2()));
            }else{
                awayScore.add(Integer.parseInt(game.getScore1()));
            }
        }



        Thread thread = new Thread(() -> {
            try {
                //get maximum value of the scores
                maxHome = Collections.max(homeScore);
                maxAway = Collections.max(awayScore);

                maxScore.add(0,maxHome);
                maxScore.add(1,maxAway);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxScore;
    }

    public ArrayList<Integer> getCheckedGamesByHomeAndAwayLastFiveByHA(String home, String away) {
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        maxScore = new ArrayList<>();

        filteredGame1 = new ArrayList<>();
        filteredGame2 = new ArrayList<>();

        filteredGameHome = new ArrayList<>();
        filteredGameAway = new ArrayList<>();

        // sort all games according to date and put them in arraylist called game1 and game2
        game1 = gameDao().getCheckedGamesByHome(home);
        game2 = gameDao().getCheckedGamesByAway(away);

        filteredGame1 = sort((ArrayList<Game>) game1);
        filteredGame2 = sort((ArrayList<Game>) game2);

        //get first 5 element of the sorted game and put them in a list called filtered list

        if (filteredGame1.size() >= 5){
            for(int i = 0; i < 5; i++) {
                filteredGameHome.add(filteredGame1.get(i));
            }
        }else{
            filteredGameHome.addAll(filteredGame1);
        }

        if (filteredGame2.size() >= 5){
            for(int i = 0; i < 5; i++) {
                filteredGameAway.add(filteredGame2.get(i));
            }
        }else{
            filteredGameAway.addAll(filteredGame2);
        }


        //put all their scores into arraylist called homescore and awayscore
        for (Game game : Objects.requireNonNull(filteredGameHome)){
            homeScore.add(Integer.parseInt(game.getScore1()));
        }

        for (Game game : Objects.requireNonNull(filteredGameAway)){
            awayScore.add(Integer.parseInt(game.getScore2()));
        }



        Thread thread = new Thread(() -> {
            try {
                //get maximum value of the scores
                maxHome = Collections.max(homeScore);
                maxAway = Collections.max(awayScore);

                maxScore.add(0,maxHome);
                maxScore.add(1,maxAway);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxScore;
    }

    public  ArrayList<Game> sort(ArrayList<Game> games){
        game1 = new ArrayList<>();
        try {
            Collections.sort(games, new Comparator<Game>() {
                @SuppressLint("SimpleDateFormat")
                @Override
                public int compare(Game t1, Game t2) {
                    if (t1.getDate() != null) {
                        String sDate1 = t1.getDate();
                        String sDate2 = t2.getDate();

                        Date date1 = null;
                        Date date2 = null;

                        try {
                            date1 = new SimpleDateFormat("MM/dd/yyyy").parse(sDate1);
                            date2 = new SimpleDateFormat("MM/dd/yyyy").parse(sDate2);
                        } catch (ParseException | NullPointerException e) {

                        }
                        if (date1 != null && date2 != null) {
                            return date2.compareTo(date1);
                        }
                    } else{
                        return 1;
                    }
                    return 0;
                }
            });
           // return games;

        } catch (IllegalArgumentException e) {

        }

        return games;
    }
    public ArrayList<Integer> getHomeAndAwayMinScore(String home, String away) {
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        minScore = new ArrayList<>();

        Thread thread = new Thread(() -> {
            try {
                game1 = gameDao().getCheckedGamesByHome(home);
                game2 = gameDao().getCheckedGamesByAway(away);

                for (Game game : Objects.requireNonNull(game1)){
                    homeScore.add(Integer.parseInt(game.getScore1()));
                }

                for (Game game : Objects.requireNonNull(game2)){
                    awayScore.add(Integer.parseInt(game.getScore2()));
                }
                minHome = Collections.min(homeScore);
                minAway = Collections.min(awayScore);

                minScore.add(0,minHome);
                minScore.add(1,minAway);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return minScore;
    }
    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }
    public ArrayList<Integer> getHomeAndAwayMostScore(String home, String away) {
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        mostScore = new ArrayList<>();

        Thread thread = new Thread(() -> {
            try {
                game1 = gameDao().getCheckedGamesByHome(home);
                game2 = gameDao().getCheckedGamesByAway(away);

                for (Game game : Objects.requireNonNull(game1)){
                    homeScore.add(Integer.parseInt(game.getScore1()));
                }
                for (Game game : Objects.requireNonNull(game2)){
                    awayScore.add(Integer.parseInt(game.getScore2()));
                }
                mostHome = mostCommon(homeScore);
                mostAway = mostCommon(awayScore);

                mostScore.add(0,mostHome);
                mostScore.add(1,mostAway);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mostScore;
    }
    public ArrayList<String> getHomeAndAwayMostScoreString(String home, String away) {
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        mostScore = new ArrayList<>();
        stringMostScore =  new ArrayList<>();

        Thread thread = new Thread(() -> {
            try {
                game1 = gameDao().getCheckedGamesByHome(home);
                game2 = gameDao().getCheckedGamesByAway(away);

                for (Game game : Objects.requireNonNull(game1)){
                    homeScore.add(Integer.parseInt(game.getScore1()));
                }

                for (Game game : Objects.requireNonNull(game2)){
                    awayScore.add(Integer.parseInt(game.getScore2()));
                }

                stringMostHome = String.valueOf(getStrings(homeScore));
                stringMostAway = String.valueOf(getStrings(awayScore));

                stringMostScore.add(0,stringMostHome);
                stringMostScore.add(1,stringMostAway);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringMostScore;
    }

    public ArrayList<String> getStrings(ArrayList<Integer> list){

        mostOccurred = new ArrayList<>();

        Set<Integer> set = new HashSet<Integer>(list);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Map<String, Long> couterMap = list.stream().collect(Collectors.groupingBy(e -> e.toString().toLowerCase(),Collectors.counting()));
            mostOccurred.add(String.valueOf(couterMap));
        }

//        for(Integer tmp:set)
//        {
//
//            mostOccurred.add(tmp +  " : " + Collections.frequency(list, tmp));
//        }
        return mostOccurred;

    }
    public ArrayList<Game> getAllCheckedGamesBySeason(int season) {
        Thread thread = new Thread(() -> {
            try {
                gameArrayList = (ArrayList<Game>) gameDao().getAllCheckedGamesBySeason(season);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameArrayList;
    }
    public LiveData<List<Game>> getAllCheckedGamesByLeagueId(int leagueId) {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getAllCheckedGamesByLeagueId(leagueId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }
    public Game getGame(final int id) {
        final Game[] finalGame = new Game[1];
        Thread thread = new Thread(() -> {
            try {
                finalGame[0] = gameDao().getGame(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalGame[0];
    }
    public LiveData<List<Game>> getGamePossibilities(final int fixtureId) {
        Thread thread = new Thread(() -> {
            try {
                game = gameDao().getGamePossibilities(fixtureId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }
    public List<Game> getGamePossibilitiess(final int fixtureId) {
        Thread thread = new Thread(() -> {
            try {
                game1 = gameDao().getGamePossibilitiess(fixtureId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return game1;
    }
    public void removeAllGame(ArrayList<Game> game) {
        new Thread( () -> {
            try {
                gameDao().deleteAll(game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void removeGame(final Game game) {
        new Thread( () -> {
            try {
                gameDao().delete(game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void addLeague(final League league) {
        new Thread(() -> {
            try {
                leagueDao().insert(league);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addLeagueList(final ArrayList<League> league) {
        new Thread(() -> {
            try {
                leagueDao().insertAll(league);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void updateLeague(final League league) {
        new Thread(() -> {
            try {
                leagueDao().update(league);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public LiveData<List<League>> getLeagues() {
        Thread thread = new Thread(() -> {
            try {
                league = leagueDao().getLeagues();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return league;
    }
    public League getLeagueById(final int id) {
        final League[] league = new League[1];
        Thread thread = new Thread(() -> {
            try {
                league[0] = leagueDao().getLeagueById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return league[0];
    }

    public void addUsage(final Usage usage) {
        new Thread(() -> {
            try {
                usageDao().insert(usage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void addUsageList(final ArrayList<Usage> usage) {
        new Thread(() -> {
            try {
                usageDao().insertAll(usage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void updateUsage(final Usage usage) {
        new Thread(() -> {
            try {
                usageDao().update(usage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public Usage getUsage(final int id) {
        final Usage[] finalUsage = new Usage[1];
        Thread thread = new Thread(() -> {
            try {
                finalUsage[0] = usageDao().getUsage(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalUsage[0];
    }

    public void deleteUsage(final Usage usage) {
        new Thread(() -> {
            try {
                usageDao().delete(usage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addInfo(final Info info) {
        new Thread(() -> {
            try {
                infoDao().insert(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void addInfoList(final ArrayList<Info> info) {
        new Thread(() -> {
            try {
                infoDao().insertAll(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void updateInfo(final Info info) {
        new Thread(() -> {
            try {
                infoDao().update(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public Info getInfo(final int id) {
        final Info[] finalInfo = new Info[1];
        Thread thread = new Thread(() -> {
            try {
                finalInfo[0] = infoDao().getInfo(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalInfo[0];
    }

    public void deleteInfo(final Info info) {
        new Thread(() -> {
            try {
                infoDao().delete(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


}


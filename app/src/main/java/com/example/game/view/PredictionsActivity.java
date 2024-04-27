package com.example.game.view;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.adapter.PredictionAdapter;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PredictionsActivity extends AppCompatActivity {
    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private PredictionAdapter adapter, filteredAdapter;
    private ListView verifyGame_ListView, myPrediction_ListView;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private Game game, theGame;
    private String baseUrl;
    private ArrayList<Integer> homeScore, awayScore, maxScore, minScore, mostOccurScore;
    private ArrayList<String> stringMostOccurScore;
    private ArrayList<Game>  homeGame, awayGame;
    private EditText maxHomeEditText, maxAwayEditText, sizeEditText, no_of_cats, highest;
    private TextView pageTitle;
    private Toolbar toolbar;
    private int maxHome, minHome, maxAway, minAway;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prediction_activity);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        baseUrl = Objects.requireNonNull(getIntent().getExtras()).getString("baseUrl");
        appViewModel.init(PredictionsActivity.this);
        appDatabase = AppDatabase.getAppDb(PredictionsActivity.this);
        verifyGame_ListView  = findViewById(R.id.prediction_ListView);
        myPrediction_ListView  = findViewById(R.id.myPrediction_ListView);
        maxHomeEditText = findViewById(R.id.maxHome);
        maxAwayEditText = findViewById(R.id.maxAway);
        sizeEditText = findViewById(R.id.size);
        pageTitle = findViewById(R.id.pageTitle);
        highest = findViewById(R.id.highest);
        no_of_cats = findViewById(R.id.no_of_cats);

        Toolbar mToolbar;
       // setHasOptionsMenu(true);
        mToolbar =  findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);

        }
        mToolbar.setTitle(null);

        mToolbar.inflateMenu(R.menu.sort_menu);

        game = new Game();
        game = (Game) this.getIntent().getExtras().getSerializable("game");

        assert game != null;
        pageTitle.setText(String.valueOf(game.getDivision()));


        ArrayList<String> usernames = new ArrayList<>();

        appDatabase.getAllCheckedGames().observe(this, games2 ->{
            for(Game game : games2){
                usernames.add(game.username);
            }
            String maxUsername = mostfrequent(usernames).toString();
            highest.setText(maxUsername);
        });


        appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
            if(!games.isEmpty()){
                theGame = games.get(0);
                maxScore = new ArrayList<>();
                ArrayList<Game> myPredictions = new ArrayList<>();
                    ArrayList<Game> no_of_schrodingers = new ArrayList<>();
                maxScore = appDatabase.getCheckedGamesByHomeAndAway(theGame.getHome(), theGame.getAway());

                try{
                    maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                    maxAwayEditText.setText(String.valueOf(maxScore.get(1)));

                    int home = maxScore.get(0);
                    int away = maxScore.get(1);

                    adapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), (ArrayList<Game>) games, R.layout.prediction_rows);

                    for (int i = 0; i < games.size(); i++) {
                        boolean gameCheck = Integer.parseInt(adapter.getGames().get(i).getScore1()) <= home && Integer.parseInt(adapter.getGames().get(i).getScore2()) <= away;
                        if (gameCheck) {
                            myPredictions.add(adapter.getGames().get(i));
                            //adapter.notifyDataSetChanged();
                        }
                        if (games.get(i).schrodinger == 1) {
                            no_of_schrodingers.add(games.get(i));
                        }
                    }

                    myPredictions.size();

                    no_of_cats.setText(String.valueOf(no_of_schrodingers.size()));

                    filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows);

                    myPrediction_ListView.setAdapter(filteredAdapter);
                    filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                    filteredAdapter.notifyDataSetChanged();

                    verifyGame_ListView.setAdapter(adapter);
                    adapter.setGames((ArrayList<Game>) adapter.getGames());
                    adapter.notifyDataSetChanged();

                    sizeEditText.setText(String.valueOf(myPredictions.size()));

                }catch (IndexOutOfBoundsException e){

                }

            }else{
                myPrediction_ListView.setAdapter(null);
                verifyGame_ListView.setAdapter(null);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                filteredAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.max:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(!games.isEmpty()){
                        theGame = games.get(0);
                        maxScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        ArrayList<Game> no_of_schrodingers = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAndAway(theGame.getHome(), theGame.getAway());

                        try{
                            maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(maxScore.get(1)));

                            int home = maxScore.get(0);
                            int away = maxScore.get(1);

                            for (int i = 0; i < games.size(); i++) {
                                boolean gameCheck = Integer.parseInt(games.get(i).getScore1()) <= home && Integer.parseInt(games.get(i).getScore2()) <= away;
                                if (gameCheck) {
                                    myPredictions.add(games.get(i));
                                }
                                if (games.get(i).schrodinger == 1) {
                                    no_of_schrodingers.add(games.get(i));
                                }
                            }

                            myPredictions.size();

                            no_of_cats.setText(String.valueOf(no_of_schrodingers.size()));

                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows);

                            myPrediction_ListView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();
                            sizeEditText.setText(String.valueOf(myPredictions.size()));

                        }catch (IndexOutOfBoundsException e){

                        }

                    }else{
                        myPrediction_ListView.setAdapter(null);
                        verifyGame_ListView.setAdapter(null);
                    }

                });
                break;
            default:
                break;
            case R.id.min:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(games != null){
                        theGame = games.get(0);
                        minScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        minScore = appDatabase.getHomeAndAwayMinScore(theGame.getHome(), theGame.getAway());
                        try{
                            maxHomeEditText.setText(String.valueOf(minScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(minScore.get(1)));

                            int home = minScore.get(0);
                            int away = minScore.get(1);

                            for (int i = 0; i < games.size(); i++) {
                                boolean gameCheck = Integer.parseInt(games.get(i).getScore1()) <= home && Integer.parseInt(games.get(i).getScore2()) <= away;
                                if (gameCheck) {
                                    myPredictions.add(games.get(i));
                                }
                            }

                            myPredictions.size();

                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows);

                            myPrediction_ListView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();
                            sizeEditText.setText(String.valueOf(myPredictions.size()));

                        }catch (IndexOutOfBoundsException ignored){}
                    }
                });
                break;
            case R.id.mostOccur:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(games != null){
                        theGame = games.get(0);
                        stringMostOccurScore = new ArrayList<>();
                        stringMostOccurScore = appDatabase.getHomeAndAwayMostScoreString(theGame.getHome(), theGame.getAway());
                        try{
                            maxHomeEditText.setText(String.valueOf(stringMostOccurScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(stringMostOccurScore.get(1)));
                        }catch (IndexOutOfBoundsException ignored){}
                    }
                });
                break;

            case R.id.lastFive:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(!games.isEmpty()){
                        theGame = games.get(0);
                        maxScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAndAwayLastFive(theGame.getHome(), theGame.getAway());
                        try{
                            int home = maxScore.get(0);
                            int away = maxScore.get(1);
                            for (int i = 0; i < games.size(); i++) {
                                boolean gameCheck = Integer.parseInt(games.get(i).getScore1()) <= home && Integer.parseInt(games.get(i).getScore2()) <= away;
                                if (gameCheck) {
                                    myPredictions.add(games.get(i));
                                }
                            }
                            myPredictions.size();
                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows);
                            myPrediction_ListView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();
                            maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(maxScore.get(1)));
                            sizeEditText.setText(String.valueOf(myPredictions.size()));

                        }catch (IndexOutOfBoundsException e){}

                    }else{
                        myPrediction_ListView.setAdapter(null);
                        verifyGame_ListView.setAdapter(null);
                    }
                });
                break;

            case R.id.lastFiveByHA:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(!games.isEmpty()){
                        theGame = games.get(0);
                        maxScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAndAwayLastFiveByHA(theGame.getHome(), theGame.getAway());
                        try{
                            int home = maxScore.get(0);
                            int away = maxScore.get(1);
                            for (int i = 0; i < games.size(); i++) {
                                boolean gameCheck = Integer.parseInt(games.get(i).getScore1()) <= home && Integer.parseInt(games.get(i).getScore2()) <= away;
                                if (gameCheck) {
                                    myPredictions.add(games.get(i));
                                }
                            }
                            myPredictions.size();
                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows);
                            myPrediction_ListView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();
                            maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(maxScore.get(1)));
                            sizeEditText.setText(String.valueOf(myPredictions.size()));
                        }catch (IndexOutOfBoundsException e){}
                    }else{
                        myPrediction_ListView.setAdapter(null);
                        verifyGame_ListView.setAdapter(null);
                    }
                });
                break;
            case R.id.mostOccurInLeague:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(games != null){
                        ArrayList<String> usernames = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();

                        theGame = games.get(1);
                        appDatabase.getAllCheckedGames().observe(this, games2 ->{
                            for(Game game : games2){
                                usernames.add(game.username);
                            }
                            String maxUsername = mostfrequent(usernames).toString();
                            ArrayList<String> words = mostfrequent(usernames);
                            for (int i = 0; i < words.size(); i++) {
                                for (Game game: games) {
                                    if (game.username.contains(words.get(i))){
                                        myPredictions.add(game);
                                    }
                                    if (Objects.equals(game.username, words.get(i))){
                                        myPredictions.add(game);
                                    }
                                }
                                highest.setText(maxUsername);
                                filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows);
                                myPrediction_ListView.setAdapter(filteredAdapter);
                                filteredAdapter.setGames((ArrayList<Game>) myPredictions);
                                sizeEditText.setText(String.valueOf(words.size()));
                            }
                        });
                    }
                });
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public ArrayList<String> mostfrequent(ArrayList<String> words){
        ArrayList<String> passwords = new ArrayList<>();
        Map<String, Long> map = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            map = words.stream()
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
            List<Map.Entry<String, Long>> result = map.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(121)
                    .collect(Collectors.toList());
            for (int i = 0; i < result.size(); i++) {
                //result.get(i).getKey();
                passwords.add(result.get(i).getKey());
            }
        }
        return passwords;
    }
    @Override
    public void onRestart() {
        super.onRestart();
    }
    @Override
    public void onStart() {
        super.onStart();
        ActiveActivitiesTracker.activityStarted(PredictionsActivity.this);
    }
    @Override
    public void onStop() {
        super.onStop();
        ActiveActivitiesTracker.activityStopped();
    }
}
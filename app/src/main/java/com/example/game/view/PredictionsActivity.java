package com.example.game.view;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.adapter.PredictionAdapter;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PredictionsActivity extends AppCompatActivity {
    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private PredictionAdapter adapter, filteredAdapter;
    private ListView verifyGame_ListView, filtered_listView;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private Game game, theGame;
    private String baseUrl;
    private ArrayList<Integer> homeScore, awayScore, maxScore, minScore, mostOccurScore;
    private ArrayList<String> stringMostOccurScore;
    private ArrayList<Game>  homeGame, awayGame;
    private EditText maxHomeEditText, maxAwayEditText, sizeEditText, no_of_cats ;
    private TextView pageTitle, highest;
    private Toolbar toolbar;
    private int maxHome, minHome, maxAway, minAway;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prediction_activity);


        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        baseUrl = Objects.requireNonNull(getIntent().getExtras()).getString("baseUrl");
        appViewModel.init(PredictionsActivity.this, baseUrl);
        appDatabase = AppDatabase.getAppDb(PredictionsActivity.this);
        filtered_listView  = findViewById(R.id.filtered_listView);
        verifyGame_ListView  = findViewById(R.id.fullScore_listView);
        maxHomeEditText = findViewById(R.id.maxHome);
        maxAwayEditText = findViewById(R.id.maxAway);
        sizeEditText = findViewById(R.id.size);
        pageTitle = findViewById(R.id.pageTitle);
        highest = findViewById(R.id.highest);
        no_of_cats = findViewById(R.id.no_of_cats);

        highest.setMovementMethod(new ScrollingMovementMethod());

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

        appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                ArrayList<Game> no_of_schrodingers = new ArrayList<>();
                for (Game game:games) {
                    if (game.getSchrodinger() == 1) {
                        no_of_schrodingers.add(game);
                    }

                }
                no_of_cats.setText(String.valueOf(no_of_schrodingers.size()));
            }
        });


        appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
            if(!games.isEmpty()){
                theGame = games.get(0);
                maxScore = new ArrayList<>();
                ArrayList<Game> myPredictions = new ArrayList<>();
                maxScore = appDatabase.getCheckedGamesByHomeAwayAndSeason(theGame.getHome(), theGame.getAway(), theGame.getSeason());


                try{
                    if (maxScore.isEmpty()){

                        ArrayList<Game> autoPrediction = (ArrayList<Game>) moveScoresToBeginning(games);

                        adapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), (ArrayList<Game>) autoPrediction, R.layout.prediction_rows, baseUrl);
                        verifyGame_ListView.setAdapter(adapter);
                        adapter.setGames((ArrayList<Game>) autoPrediction);

                        filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), autoPrediction, R.layout.prediction_rows, baseUrl);
                        filtered_listView.setAdapter(filteredAdapter);
                        filteredAdapter.setGames((ArrayList<Game>) autoPrediction);


                    }else{


                        maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                        maxAwayEditText.setText(String.valueOf(maxScore.get(1)));

                        int home = maxScore.get(0);
                        int away = maxScore.get(1);

                        ArrayList<Game> autoPrediction = (ArrayList<Game>) moveScoresToBeginning(games);

                        adapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), (ArrayList<Game>) autoPrediction, R.layout.prediction_rows, baseUrl);

                        for (int i = 0; i < autoPrediction.size(); i++) {
                            if(autoPrediction.get(i).getScore1() == null){

                            }
                            else {
                                boolean gameCheck = Integer.parseInt(autoPrediction.get(i).getScore1()) <= home && Integer.parseInt(autoPrediction.get(i).getScore2()) <= away;
                                if (gameCheck) {
                                    myPredictions.add(autoPrediction.get(i));
                                }
                            }
                        }

                        myPredictions.size();

                        filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows, baseUrl);

                        filtered_listView.setAdapter(filteredAdapter);
                        filteredAdapter.setGames((ArrayList<Game>) myPredictions);

                        verifyGame_ListView.setAdapter(adapter);
                        adapter.setGames((ArrayList<Game>) autoPrediction);

                        sizeEditText.setText(String.valueOf(myPredictions.size()));
                    }
                }catch (IndexOutOfBoundsException e){

                }

            }else{
                filtered_listView.setAdapter(null);
                verifyGame_ListView.setAdapter(null);
            }

        });

    }
    public static List<Game> moveScoresToBeginning(List<Game> games) {

        ArrayList<Game> gameToLoop = (ArrayList<Game>) games;
        ArrayList<List<Integer>> group = new ArrayList<List<Integer>>();
        group.add(Arrays.asList(0,0));
        group.add(Arrays.asList(0,1));
        group.add(Arrays.asList(1,0));
        group.add(Arrays.asList(1,1));
        group.add(Arrays.asList(2,0));

        group.add(Arrays.asList(0,2));
        group.add(Arrays.asList(2,1));
        group.add(Arrays.asList(1,2));
        group.add(Arrays.asList(2,2));
        group.add(Arrays.asList(3,0));


        group.add(Arrays.asList(0,3));
        group.add(Arrays.asList(3,1));
        group.add(Arrays.asList(1,3));
        group.add(Arrays.asList(2,3));
        group.add(Arrays.asList(3,2));

        group.add(Arrays.asList(3,3));
        group.add(Arrays.asList(0,4));
        group.add(Arrays.asList(4,0));
        group.add(Arrays.asList(4,1));
        group.add(Arrays.asList(1,4));

        group.add(Arrays.asList(4,2));
        group.add(Arrays.asList(2,4));
        group.add(Arrays.asList(4,3));
        group.add(Arrays.asList(3,4));
        group.add(Arrays.asList(4,4));



        List<Game> newGames = new ArrayList<Game>();


        for (List<Integer> nested : group) {
            for (Game game : gameToLoop) {
                if(game.getScore1() == null){

                }else{
                    if (Integer.parseInt(game.getScore1()) == nested.get(0) && Integer.parseInt(game.getScore2()) == nested.get(1)){
                        newGames.add(game);
                    }
                }

            }
        }

        gameToLoop.removeAll(newGames);
        newGames.addAll(gameToLoop);

        return newGames;
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
                try{adapter.getFilter().filter(newText);}catch(NullPointerException e){}
                filteredAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
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
                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows, baseUrl);
                            filtered_listView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();
                            sizeEditText.setText(String.valueOf(myPredictions.size()));

                        }catch (IndexOutOfBoundsException e){

                        }

                    }else{
                        filtered_listView.setAdapter(null);
                        verifyGame_ListView.setAdapter(null);
                    }

                });
                break;
            case R.id.maxbyseason:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(!games.isEmpty()){
                        theGame = games.get(0);
                        maxScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        ArrayList<Game> no_of_schrodingers = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAwayAndSeason(theGame.getHome(), theGame.getAway(), theGame.getSeason());
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
                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows, baseUrl);
                            filtered_listView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();
                            sizeEditText.setText(String.valueOf(myPredictions.size()));

                        }catch (IndexOutOfBoundsException e){

                        }

                    }else{
                        filtered_listView.setAdapter(null);
                        verifyGame_ListView.setAdapter(null);
                    }

                });
                break;
            case R.id.min:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(games != null){
                        theGame = games.get(0);
                        minScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        ArrayList<Game> no_of_schrodingers = new ArrayList<>();
                        minScore = appDatabase.getHomeAndAwayMinScore(theGame.getHome(), theGame.getAway(), theGame.getSeason());
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
                                if (games.get(i).schrodinger == 1) {
                                    no_of_schrodingers.add(games.get(i));
                                }
                            }

                            myPredictions.size();
                            no_of_cats.setText(String.valueOf(no_of_schrodingers.size()));
                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows, baseUrl);

                            filtered_listView.setAdapter(filteredAdapter);
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
                        stringMostOccurScore = appDatabase.getHomeAndAwayMostScoreString(theGame.getHome(), theGame.getAway(), theGame.getSeason());
                        try{
                            maxHomeEditText.setText(String.valueOf(stringMostOccurScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(stringMostOccurScore.get(1)));
                        }catch (IndexOutOfBoundsException ignored){}
                    }
                });
                break;
            case R.id.mostOccurHA:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(games != null){
                        theGame = games.get(0);
                        stringMostOccurScore = new ArrayList<>();
                        stringMostOccurScore = appDatabase.getHomeAndAwayMostScoreString2(theGame.getHome(), theGame.getAway(), theGame.getSeason());
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
                        ArrayList<Game> no_of_schrodingers = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAndAwayLastFive(theGame.getHome(), theGame.getAway(), theGame.getSeason());
                        try{
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
                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows, baseUrl);
                            filtered_listView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();
                            maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(maxScore.get(1)));
                            sizeEditText.setText(String.valueOf(myPredictions.size()));

                        }catch (IndexOutOfBoundsException e){}

                    }else{
                        filtered_listView.setAdapter(null);
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
                        ArrayList<Game> no_of_schrodingers = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAndAwayLastFiveByHA(theGame.getHome(), theGame.getAway(), theGame.getSeason());
                        try{
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
                            filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), myPredictions, R.layout.prediction_rows, baseUrl);
                            filtered_listView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();
                            maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(maxScore.get(1)));
                            sizeEditText.setText(String.valueOf(myPredictions.size()));
                        }catch (IndexOutOfBoundsException e){}
                    }else{
                        filtered_listView.setAdapter(null);
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
                                    if (game.username.contains(words.get(i)) || game.username.equals(words.get(i))){
                                        myPredictions.add(game);
                                    }
                                    if (Objects.equals(game.username, words.get(i))){
                                        myPredictions.add(game);
                                    }
                                }
                                highest.setText(maxUsername);
                                List<Game> unique = myPredictions.stream()
                                        .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(Game::getId))),
                                                ArrayList::new));
                                filteredAdapter = new PredictionAdapter(getSystemService(PredictionsActivity.class), (ArrayList<Game>) unique, R.layout.prediction_rows, baseUrl);
                                filtered_listView.setAdapter(filteredAdapter);
                                filteredAdapter.setGames((ArrayList<Game>) unique);
                                sizeEditText.setText(String.valueOf(unique.size()));
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
//    public ArrayList<String> mostfrequent(ArrayList<String> words){
//        ArrayList<String> passwords = new ArrayList<>();
//        for (int i = 0; i < words.size(); i++) {
//            if ( words.toString().toLowerCase().indexOf(words.get(i).toLowerCase()) != -1 ) {
//                passwords.add(words.get(i));
//                System.out.println("I found the keyword");
//            } else {
//                System.out.println("not found");
//            }
//        }
//        return passwords;
//    }
//    public ArrayList<String> mostfrequent(ArrayList<String> words){
//        ArrayList<String> passwords = new ArrayList<>();
//        for (int i = 0; i < words.size(); i++) {
//            if (words.toString().toLowerCase().contains(words.get(i).toLowerCase())) {
//                passwords.add(words.get(i));
//            }
//        }
//        return passwords;
//    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        //outState.clear();
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
    @Override
    public void onPause() {
        super.onPause();
        //ActiveActivitiesTracker.activityStopped();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
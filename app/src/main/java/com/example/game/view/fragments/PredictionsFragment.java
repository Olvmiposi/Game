package com.example.game.view.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.adapter.PredictionAdapter;
import com.example.game.model.Game;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PredictionsFragment extends Fragment implements IOnBackPressed {
    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private PredictionAdapter adapter, filteredAdapter;
    private ListView verifyGame_ListView, myPrediction_ListView;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private Bundle bundle;
    private Game game;
    private String baseUrl;
    private ArrayList<Integer> homeScore, awayScore, maxScore, minScore, mostOccurScore;
    private ArrayList<String> stringMostOccurScore;
    private ArrayList<Game>  homeGame, awayGame;
    private EditText maxHomeEditText, maxAwayEditText, sizeEditText;
    private TextView pageTitle;

    private int maxHome, minHome, maxAway, minAway;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prediction_activity, container, false);

        Toolbar mToolbar;
        setHasOptionsMenu(true);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_home);
        if (mToolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        }
        mToolbar.setTitle(null);

        mToolbar.inflateMenu(R.menu.sort_menu);

        return view;
        //return inflater.inflate(R.layout.prediction_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bundle = getArguments();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        baseUrl = bundle.getString("baseUrl");
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getContext(), baseUrl);
        appDatabase = AppDatabase.getAppDb(getContext());
        verifyGame_ListView  = getView().findViewById(R.id.prediction_ListView);
        myPrediction_ListView  = getView().findViewById(R.id.myPrediction_ListView);
        maxHomeEditText = getView().findViewById(R.id.maxHome);
        maxAwayEditText = getView().findViewById(R.id.maxAway);
        sizeEditText = getView().findViewById(R.id.size);
        pageTitle = getView().findViewById(R.id.pageTitle);
        //((MainActivity) requireActivity()).disableSwipe();
        //((AppCompatActivity) requireActivity()).getSupportActionBar().hide();

        game = new Game();
        game = (Game) bundle.getSerializable("game");

        adapter = new PredictionAdapter();

        pageTitle.setText(String.valueOf(game.getDivision()));

        myUpdateOperation();


    }

    public void myUpdateOperation(){

        appDatabase.getGamePossibilities(game.getFixtureId()).observe(getViewLifecycleOwner(), games -> {
            if(!games.isEmpty()){
                Game theGame = new Game();
                theGame = games.get(0);
                maxScore = new ArrayList<>();
                ArrayList<Game> myPredictions = new ArrayList<>();
                maxScore = appDatabase.getCheckedGamesByHomeAndAway(theGame.getHome(), theGame.getAway(), theGame.getSeason());

                try{
                    maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                    maxAwayEditText.setText(String.valueOf(maxScore.get(1)));

                    int home = maxScore.get(0);
                    int away = maxScore.get(1);

                    ArrayList<Game> autoPrediction = (ArrayList<Game>) moveScoresToBeginning(games);

                    adapter = new PredictionAdapter(getActivity(), (ArrayList<Game>) autoPrediction, R.layout.prediction_rows, baseUrl);

                    for (int i = 0; i < games.size(); i++) {
                        boolean gameCheck = Integer.parseInt(adapter.getGames().get(i).getScore1()) <= home && Integer.parseInt(adapter.getGames().get(i).getScore2()) <= away;
                        if (gameCheck) {
                            myPredictions.add(adapter.getGames().get(i));
                        }
                    }

                    myPredictions.size();
                    sizeEditText.setText(String.valueOf(myPredictions.size()));

                    filteredAdapter = new PredictionAdapter(getActivity(), myPredictions, R.layout.prediction_rows, baseUrl);


                    myPrediction_ListView.setAdapter(filteredAdapter);
                    filteredAdapter.setGames((ArrayList<Game>) myPredictions);

                    verifyGame_ListView.setAdapter(adapter);
                    adapter.setGames((ArrayList<Game>) autoPrediction);



                }catch (IndexOutOfBoundsException e){

                }

            }else{
                myPrediction_ListView.setAdapter(null);
                verifyGame_ListView.setAdapter(null);
            }

        });

    }

    public static List<Game> moveScoresToBeginning(final List<Game> games) {
        List<Integer> scores = Arrays.asList((0), (1), (2));

        ArrayList<List<Integer>> group = new ArrayList<List<Integer>>();
        group.add(Arrays.asList(0,0));
        group.add(Arrays.asList(0,1));
        group.add(Arrays.asList(1,0));
        group.add(Arrays.asList(1,1));
        group.add(Arrays.asList(2,0));

        group.add(Arrays.asList(0,2));
        group.add(Arrays.asList(2,1));
        group.add(Arrays.asList(2,2));
        group.add(Arrays.asList(1,2));
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



        final List<Game> newGames = new ArrayList<Game>();
        final List<Game> otherGames = new ArrayList<Game>();

        for (Game game : games) {
            for (List<Integer> nested : group) {
                if (Integer.parseInt(game.score1)  == nested.get(0) && Integer.parseInt(game.score2)  == nested.get(1)){
                    newGames.add(game);
                }else{
                    otherGames.add(game);
                }

            }
        }
        newGames.addAll(otherGames);

        return newGames;
    }


    @Override
    public void onRefresh() {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        //searchMenuItem.expandActionView();
        //searchView.setIconified(true);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    adapter.getFilter().filter(newText);
                }catch (NullPointerException e){

                }
                return false;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.max:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(!games.isEmpty()){
                        Game theGame = new Game() ;
                        theGame = games.get(0);
                        maxScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAndAway(theGame.getHome(), theGame.getAway(), theGame.getSeason());

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
                            }

                            myPredictions.size();

                            filteredAdapter = new PredictionAdapter(getActivity(), myPredictions, R.layout.prediction_rows, baseUrl);


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
            case R.id.min:
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(games != null){
                        Game theGame = new Game() ;
                        theGame = games.get(0);
                        minScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
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
                            }

                            myPredictions.size();

                            filteredAdapter = new PredictionAdapter(getActivity(), myPredictions, R.layout.prediction_rows, baseUrl);


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
                        Game theGame = new Game() ;
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

            case R.id.lastFive:
                //myPrediction_ListView.setAdapter(null);
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(!games.isEmpty()){
                        Game theGame = new Game() ;
                        theGame = games.get(0);
                        maxScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAndAwayLastFive(theGame.getHome(), theGame.getAway(), theGame.getSeason());
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

                            filteredAdapter = new PredictionAdapter(getActivity(), myPredictions, R.layout.prediction_rows, baseUrl);


                            myPrediction_ListView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();

                            maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(maxScore.get(1)));
                            sizeEditText.setText(String.valueOf(myPredictions.size()));

                        }catch (IndexOutOfBoundsException e){

                        }

                    }else{
                        myPrediction_ListView.setAdapter(null);
                        verifyGame_ListView.setAdapter(null);
                    }

                });
                
                break;

            case R.id.lastFiveByHA:
                //myPrediction_ListView.setAdapter(null);
                appDatabase.getGamePossibilities(game.getFixtureId()).observe(this, games -> {
                    if(!games.isEmpty()){
                        Game theGame = new Game() ;
                        theGame = games.get(0);
                        maxScore = new ArrayList<>();
                        ArrayList<Game> myPredictions = new ArrayList<>();
                        maxScore = appDatabase.getCheckedGamesByHomeAndAwayLastFiveByHA(theGame.getHome(), theGame.getAway(), theGame.getSeason());
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

                            filteredAdapter = new PredictionAdapter(getActivity(), myPredictions, R.layout.prediction_rows, baseUrl);


                            myPrediction_ListView.setAdapter(filteredAdapter);
                            filteredAdapter.setGames((ArrayList<Game>) filteredAdapter.getGames());
                            filteredAdapter.notifyDataSetChanged();

                            maxHomeEditText.setText(String.valueOf(maxScore.get(0)));
                            maxAwayEditText.setText(String.valueOf(maxScore.get(1)));
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
        }
        super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        requireActivity().getMenuInflater().inflate(R.menu.sort_menu, menu);
        searchMenuItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView=(SearchView) searchMenuItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setQueryHint("username");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    adapter.getFilter().filter(newText);
                }catch (NullPointerException e){

                }
                return false;
            }
        });
    }
}
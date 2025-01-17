package com.example.game.businessLogic;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.model.Game;
import com.example.game.model.Info;
import com.example.game.model.League;
import com.example.game.model.Usage;
import com.example.game.repository.AppDatabase;
import com.example.game.view.fragments.AllGamesFragmentActivity;
import com.example.game.view.fragments.BetFragment;
import com.example.game.view.fragments.CallApiFragment;
import com.example.game.view.fragments.HomeFragment;
import com.example.game.view.fragments.PasswordsFragmentActivity;
import com.example.game.view.fragments.SchrodingerFragmentActivity;
import com.example.game.view.fragments.SearchFragment;
import com.example.game.view.fragments.TableFragmentActivity;
import com.example.game.viewModel.AppViewModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivityBL {

    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private ArrayList<Integer> allGamesLeaguesId,allPasswordLeaguesId,allSchrodingerLeaguesId;
    private League league ;
    private ArrayList<League> allPasswordLeagues, allUniqueLeagues, allLeagues;
    private Usage usage, databaseUsage;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private TextView available_usernames, total_games_count, network_status_text;
    private Button schrodingerBtn, passwordBtn;
    private ImageButton network_status ;
    private ArrayList<Game> games;
    private Info info;
    private Context context;
    private Activity activity;
    private String latestDate, baseUrl ;




    public MainActivityBL(Activity activity,AppViewModel appViewModel, AppDatabase appDatabase, ArrayList<Integer> allGamesLeaguesId, ArrayList<Integer> allPasswordLeaguesId, ArrayList<Integer> allSchrodingerLeaguesId, League league, ArrayList<League> allPasswordLeagues, ArrayList<League> allUniqueLeagues, Usage usage, Usage databaseUsage, SwipeRefreshLayout mySwipeRefreshLayout, TextView available_usernames, TextView total_games_count, TextView network_status_text, Button schrodingerBtn, Button passwordBtn, ImageButton network_status, ArrayList<Game> games, Info info, Context context, String baseUrl) {
        this.activity = activity;
        this.appViewModel = appViewModel;
        this.appDatabase = appDatabase;
        this.allGamesLeaguesId = allGamesLeaguesId;
        this.allPasswordLeaguesId = allPasswordLeaguesId;
        this.allSchrodingerLeaguesId = allSchrodingerLeaguesId;
        this.league = league;
        this.allPasswordLeagues = allPasswordLeagues;
        this.allUniqueLeagues = allUniqueLeagues;
        this.usage = usage;
        this.databaseUsage = databaseUsage;
        this.mySwipeRefreshLayout = mySwipeRefreshLayout;
        this.available_usernames = available_usernames;
        this.total_games_count = total_games_count;
        this.network_status_text = network_status_text;
        this.schrodingerBtn = schrodingerBtn;
        this.passwordBtn = passwordBtn;
        this.network_status = network_status;
        this.games = games;
        this.info = info;
        this.context = context;
        this.baseUrl = baseUrl;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update(){

        //get all league id from available games
        allGamesLeaguesId = new ArrayList<>();
        allPasswordLeaguesId = new ArrayList<>();
        allSchrodingerLeaguesId = new ArrayList<>();
        league = new League();

        allPasswordLeagues = new ArrayList<>();
        allUniqueLeagues = new ArrayList<>();
//
        allLeagues = new ArrayList<>();
        ArrayList<Integer> leagueIdArrayList = new ArrayList<>();

        allLeagues = (ArrayList<League>) appDatabase.getLeagues();


        for (League league : allLeagues) {
            leagueIdArrayList.add(league.getLeagueId());
        }
        ArrayList<Integer> uniqueList2 = new ArrayList<>();

        for (int id: leagueIdArrayList) {
            if(!uniqueList2.contains(id)){
                uniqueList2.add(id);
                League newLeague = appDatabase.getLeagueById(id);
                allUniqueLeagues.add(newLeague);
            }
        }


        //latestDate = String.valueOf(appDatabase.getLatestDate());

        appDatabase.getGames().observe((LifecycleOwner) activity, new Observer<List<Game>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<Game> games) {
                if(games == null){
                }else {
                    for(Game game : games ) {
                        if(!allGamesLeaguesId.contains(game.getLeagueId()))
                            allGamesLeaguesId.add(game.getLeagueId());
                    }

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);

                    try {
                        latestDate = Objects.requireNonNull(games.stream()
                                .map(d -> LocalDate.parse(String.valueOf(d.getDate()), dtf))
                                .min(Comparator.comparing(LocalDate::toEpochDay))
                                .orElse(null)).format(dtf);
                    }catch (NullPointerException e){}



                }
            }
        });


        games = appDatabase.getAllCheckedGamesList();
        for(Game game : games) {
            league = appDatabase.getLeagueByIdAndSeason(game.getLeagueId(), game.getSeason());
            if(!allPasswordLeagues.contains(league))
                allPasswordLeagues.add(league);
        }



        // for api 3001 i.e 2024api
        appDatabase.getSchrodingerGames().observe((LifecycleOwner) activity, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games == null){
                }else {
                    for(Game game : games ) {
                        if(!allSchrodingerLeaguesId.contains(game.getLeagueId()))
                            allSchrodingerLeaguesId.add(game.getLeagueId());
                    }
                }
            }
        });


        // for api 3000 i.e 2018api
//        games = appDatabase.getSchrodingersGames();
//
//        try{
//            for(Game game : games ) {
//                if(!allSchrodingerLeaguesId.contains(game.getLeagueId()))
//                    allSchrodingerLeaguesId.add(game.getLeagueId());
//            }
//        }catch (NullPointerException e){}





        passwordBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int isInvinsible = 1;
                PasswordsFragmentActivity passwordsFragmentActivity = new PasswordsFragmentActivity();
                Bundle args = new Bundle();
                args.putString("baseUrl", baseUrl);
                args.putSerializable("allPasswordLeagues",(Serializable)allPasswordLeagues);
                args.putInt("isInvinsible", isInvinsible);
                passwordsFragmentActivity.setArguments(args);
                appViewModel.replaceFragment(passwordsFragmentActivity, v);

                return true;
            }
        });

        schrodingerBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int isInvinsible = 1;
                SchrodingerFragmentActivity schrodingerFragmentActivity = new SchrodingerFragmentActivity();
                Bundle args = new Bundle();
                args.putString("baseUrl", baseUrl);
                args.putSerializable("allSchrodingerLeaguesId", (Serializable)allSchrodingerLeaguesId);
                args.putSerializable("allPasswordLeagues",(Serializable)allPasswordLeagues);
                args.putInt("isInvinsible", isInvinsible);
                schrodingerFragmentActivity.setArguments(args);
                appViewModel.replaceFragment(schrodingerFragmentActivity, v);

                return true;
            }
        });

    }

    public void setInfo(){

        try{

            info = appDatabase.getInfo(1);

            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true);
            if (info != null) {
                total_games_count.setVisibility(View.VISIBLE);
                total_games_count.setText(myFormat.format(info.getNo_of_games()));

                available_usernames.setVisibility(View.VISIBLE);
                available_usernames.setText(myFormat.format((info.getAvailable_username())));
            }
            else if (info.getAvailable_username()/121 < 121 ){
                available_usernames.setVisibility(View.GONE);
            }
            appViewModel.getUsernameInfoResponse().observe((LifecycleOwner) activity, new Observer<Info>() {
                @Override
                public void onChanged(Info info) {
                    try{
                        if (info != null) {
                            network_status.setImageResource( R.drawable.online_24);
                            network_status_text.setText("online");
                        }
                        else if (info.getAvailable_username()/121 < 121 ){
                            available_usernames.setVisibility(View.GONE);
                        }
                    }catch (NullPointerException | IllegalStateException e){
                        network_status.setImageResource( R.drawable.offline_24);
                        network_status_text.setText("offline");
                    }

                }
            });

        }catch(NullPointerException e){

        }

    }
    public void updateUsage(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();

        // today's date
        usage = new Usage();
        usage.setId(1);
        usage.setLast_used(dateFormat.format(date));

        databaseUsage = new Usage();
        databaseUsage = appDatabase.getUsage(1);

        if (databaseUsage == null){
            appDatabase.deleteUsage(usage);
            appDatabase.addUsage(usage);
        }else{
            // update database with new usage
            appDatabase.updateUsage(usage);
        }
        if (databaseUsage != null){
            // update database with new usage
            appDatabase.updateUsage(usage);
        }
    }

    public void onCompletion() {
        if (mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void goHome(View view){
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        homeFragment.setArguments(args);
        appViewModel.replaceFragment(homeFragment, view);
    }

    public void openBetActivity(View view) {
        BetFragment betFragment = new BetFragment();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        betFragment.setArguments(args);
        appViewModel.replaceFragment(betFragment, view);
    }

    public void callApi(View view) {
        CallApiFragment callApiFragment = new CallApiFragment();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        args.putSerializable("allUniqueLeagues",(Serializable)allUniqueLeagues);
        callApiFragment.setArguments(args);
        appViewModel.replaceFragment(callApiFragment, view);
    }

    public void allGame(View view) {
        AllGamesFragmentActivity allGamesFragment = new AllGamesFragmentActivity();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        args.putSerializable("allGamesLeaguesId",(Serializable)allGamesLeaguesId);
        args.putSerializable("allPasswordLeagues",(Serializable)allPasswordLeagues);
        args.putString("latestDate", latestDate);
        allGamesFragment.setArguments(args);
        appViewModel.replaceFragment(allGamesFragment, view);

    }
    public void schrodinger(View view) {
        int isInvinsible = 0;
        SchrodingerFragmentActivity schrodingerFragment = new SchrodingerFragmentActivity();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        args.putSerializable("allSchrodingerLeaguesId", (Serializable)allSchrodingerLeaguesId);
        args.putSerializable("allPasswordLeagues",(Serializable)allPasswordLeagues);
        args.putString("latestDate", latestDate);
        args.putInt("isInvinsible", isInvinsible);
        schrodingerFragment.setArguments(args);

        appViewModel.replaceFragment(schrodingerFragment, view);


    }
    public void table(View view) {
        TableFragmentActivity tableFragment = new TableFragmentActivity();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        tableFragment.setArguments(args);
        appViewModel.replaceFragment(tableFragment, view);

    }
    public void passwords(View view) {
        int isInvinsible = 0;
        PasswordsFragmentActivity passwordFragment = new PasswordsFragmentActivity();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        args.putSerializable("allPasswordLeagues",(Serializable)allPasswordLeagues);
        args.putInt("isInvinsible", isInvinsible);
        passwordFragment.setArguments(args);

        appViewModel.replaceFragment(passwordFragment, view);

    }

    public void search(View view) {
        int isInvinsible = 0;
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        args.putInt("isInvinsible", isInvinsible);
        searchFragment.setArguments(args);
        appViewModel.replaceFragment(searchFragment, view);
    }

}

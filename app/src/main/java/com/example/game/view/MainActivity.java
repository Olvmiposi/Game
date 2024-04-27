package com.example.game.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.model.Game;
import com.example.game.model.Info;
import com.example.game.model.League;
import com.example.game.model.Usage;
import com.example.game.model.User;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.service.MainMenuActivityService;
import com.example.game.view.fragments.AllGamesFragmentActivity;
import com.example.game.view.fragments.BetFragment;
import com.example.game.view.fragments.CallApiFragment;
import com.example.game.view.fragments.HomeFragment;
import com.example.game.view.fragments.LeagueFragment;
import com.example.game.view.fragments.PasswordsFragmentActivity;
import com.example.game.view.fragments.SchrodingerFragment;
import com.example.game.view.fragments.SchrodingerFragmentActivity;
import com.example.game.view.fragments.TableFragment;
import com.example.game.view.fragments.TableFragmentActivity;
import com.example.game.viewModel.AppViewModel;
import com.example.game.viewModel.MyApplication;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements IOnBackPressed {

    private AppViewModel appViewModel;
    private MainMenuActivityService menuActivityService;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private AppDatabase appDatabase;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private TextView available_usernames, total_games_count;
    private TextView network_status_text ;
    private ImageButton network_status ;
    private ProgressBar progressBar;
    private Usage usage, databaseUsage;
    private String maxDate;
    private MyApplication myApplication;
    private Button schrodingerBtn, passwordBtn;
    private ArrayList<Game> games;
    private LinearLayout passwordLayout;
    private Info info;
    private Context context;
    private ArrayList<Integer> allGamesLeaguesId,allPasswordLeaguesId,allSchrodingerLeaguesId, homeScore, awayScore;

    private League league ;
    private ArrayList<League> allGamesLeagues,allPasswordLeagues,allSchrodingerLeagues;
    private ArrayList<Game>  homeGame, awayGame;
    private int maxHome, minHome, maxAway, minAway;
    private BottomNavigationView bottomNav;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        context = getBaseContext();
        myApplication = new MyApplication();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.init(context);
        appDatabase = AppDatabase.getAppDb(context);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();

        // today's date
        usage = new Usage();
        usage.setId(1);
        usage.setLast_used(dateFormat.format(date));



        available_usernames = findViewById(R.id.available_usernames);
        total_games_count = findViewById(R.id.total_games_count);
        network_status_text = findViewById(R.id.network_status_text);
        network_status = findViewById(R.id.network_status);
        progressBar = findViewById(R.id.progressBar);
        schrodingerBtn = findViewById(R.id.schrodingerBtn);
        passwordBtn = findViewById(R.id.passwordBtn);

        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);

        progressBar.setVisibility(View.GONE);

        updateUsage();
        setInfo();

        View rootView = findViewById(android.R.id.content);

        // as soon as the application opens the first
        // fragment should be shown to the user
        // in this case it is algorithm fragment
        //getSupportFragmentManager().beginTransaction().replace(R.id.layout_placeholder, new HomeFragment()).commit();


        goHome(rootView);



        appViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    progressBar.setVisibility(View.VISIBLE);
                }else{
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        //get all league id from available games
        allGamesLeaguesId = new ArrayList<>();
        allPasswordLeaguesId = new ArrayList<>();
        allSchrodingerLeaguesId = new ArrayList<>();
        league = new League();
        allPasswordLeagues = new ArrayList<>();

        appDatabase.getGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games == null){
                }else {
                    for(Game game : games ) {
                        if(!allGamesLeaguesId.contains(game.getLeagueId()))
                            allGamesLeaguesId.add(game.getLeagueId());

                    }
                }
            }
        });


        games = appDatabase.getAllCheckedGamesList();
        for(Game game : games) {
            league = appDatabase.getLeagueByIdAndSeason(game.getLeagueId(), game.getSeason());
            if(!allPasswordLeagues.contains(league))
                allPasswordLeagues.add(league);
        }

        appDatabase.getAllCheckedGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(List<Game> games) {
                if(games == null){
                }else {
                    for(Game game : games ) {
                        if(!allPasswordLeaguesId.contains(game.getLeagueId()))
                            allPasswordLeaguesId.add(game.getLeagueId());
                    }
                }
            }
        });

        // for api 3001 i.e 2024api
        appDatabase.getSchrodingerGames().observe(this, new Observer<List<Game>>() {
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

                LeagueFragment allGamesFragment = new LeagueFragment();
                Bundle args = new Bundle();
                args.putSerializable("allPasswordLeaguesId",(Serializable)allPasswordLeaguesId);
                args.putString("allPasswordLeaguesIdBundle",args.toString());
                args.putInt("isInvinsible", isInvinsible);
                allGamesFragment.setArguments(args);
                appViewModel.showFragment(allGamesFragment, v);

                return true;
            }
        });

        schrodingerBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int isInvinsible = 1;
                LeagueFragment allGamesFragment = new LeagueFragment();
                Bundle args = new Bundle();
                args.putSerializable("allSchrodingerLeaguesId", (Serializable)allSchrodingerLeaguesId);
                args.putString("schrodingerargs",args.toString());
                args.putInt("isInvinsible", isInvinsible);
                allGamesFragment.setArguments(args);
                appViewModel.showFragment(allGamesFragment, v);

                return false;
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

            appViewModel.getUsernameInfoResponse().observe(this, new Observer<Info>() {
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


    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        User loggedInUser = appDatabase.getLoggedInUser();
        switch (item.getItemId()) {
            case R.id.nav_logout:
                appDatabase.removeUser(loggedInUser);
                Intent intent = new Intent(this, SplashScreen.class);
                startActivity(intent);
                getBaseContext().getSharedPreferences("myKey", 0).edit().clear().commit();
                appDatabase.clearAllTables();
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCompletion() {
        if (mySwipeRefreshLayout.isRefreshing()) {
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void goHome(View view){
        HomeFragment homeFragment = new HomeFragment();
        appViewModel.showFragment(homeFragment, view);
    }

    public void openBetActivity(View view) {
        BetFragment betFragment = new BetFragment();
        appViewModel.showFragment(betFragment, view);

    }

    public void callApi(View view) {
        CallApiFragment callApiFragment = new CallApiFragment();
        appViewModel.showFragment(callApiFragment, view);
    }

    public void allGame(View view) {
        AllGamesFragmentActivity allGamesFragment = new AllGamesFragmentActivity();
        Bundle args = new Bundle();
        args.putSerializable("allGamesLeaguesId",(Serializable)allGamesLeaguesId);
        args.putString("allGamesLeaguesIdBundle",args.toString());
        allGamesFragment.setArguments(args);
        appViewModel.showFragment(allGamesFragment, view);
    }
    public void schrodinger(View view) {
        SchrodingerFragmentActivity schrodingerFragment = new SchrodingerFragmentActivity();
        Bundle args = new Bundle();
        args.putSerializable("allSchrodingerLeaguesId", (Serializable)allSchrodingerLeaguesId);
        args.putString("schrodingerargs",args.toString());
        schrodingerFragment.setArguments(args);
        appViewModel.showFragment(schrodingerFragment, view);

    }
    public void table(View view) {
        TableFragmentActivity tableFragment = new TableFragmentActivity();
        Bundle args = new Bundle();
        args.putSerializable("allGamesLeaguesId",(Serializable)allGamesLeaguesId);
        args.putString("tableargs",args.toString());
        tableFragment.setArguments(args);
        appViewModel.showFragment(tableFragment, view);

    }
    public void passwords(View view) {
        int isInvinsible = 0;
        PasswordsFragmentActivity passwordFragment = new PasswordsFragmentActivity();
        Bundle args = new Bundle();
        args.putSerializable("allPasswordLeagues",(Serializable)allPasswordLeagues);
        args.putInt("isInvinsible", isInvinsible);
        passwordFragment.setArguments(args);
        appViewModel.showFragment(passwordFragment, view);

    }
    @Override
    public void onStart() {
        super.onStart();
        ActiveActivitiesTracker.activityStarted(this.getBaseContext());
    }
    @Override
    public void onStop() {
        super.onStop();
        ActiveActivitiesTracker.activityStopped();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void disableSwipe(){
        mySwipeRefreshLayout.setEnabled(false);
    }
    @Override
    public void onRefresh(){
        Intent serviceintent = new Intent (this, MainMenuActivityService.class);
        //startService(serviceintent);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            startService(serviceintent);
            onCompletion();
        });
    }

    //disables back pressed button on this activity and all fragments
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        HomeFragment homeFragment = new HomeFragment();
        View rootView = findViewById(android.R.id.content);
        try{
            if(Objects.equals(appViewModel.getTopFragment(rootView).getClass().getSimpleName(), homeFragment.getClass().getSimpleName())){
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
                    return true;
                }
            }
        }catch (NullPointerException e){}

        return super.onKeyDown(keyCode, event);
    }
}
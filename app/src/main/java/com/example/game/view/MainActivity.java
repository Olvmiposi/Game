package com.example.game.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.game.R;
import com.example.game.businessLogic.MainActivityBL;
import com.example.game.model.Game;
import com.example.game.model.Info;
import com.example.game.model.League;
import com.example.game.model.Usage;
import com.example.game.model.User;
import com.example.game.repository.AppDatabase;
import com.example.game.service.IOnBackPressed;
import com.example.game.service.MainMenuActivityService;
import com.example.game.view.fragments.HomeFragment;
import com.example.game.viewModel.AppViewModel;
import com.example.game.viewModel.MyApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements IOnBackPressed {

    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private MainMenuActivityService menuActivityService;
    private TextView available_usernames, total_games_count, network_status_text;
    private ImageButton network_status ;
    private ProgressBar progressBar;
    private Usage usage, databaseUsage;
    private MyApplication myApplication;
    private Button schrodingerBtn, passwordBtn;
    private ArrayList<Game> games;
    private Info info;
    private Context context;
    private ArrayList<Integer> allGamesLeaguesId,allPasswordLeaguesId,allSchrodingerLeaguesId;
    private League league ;
    private ArrayList<League> allPasswordLeagues, allUniqueLeagues;
    private MainActivityBL mainActivityBL;

    private String baseUrl;
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getBaseContext();
        myApplication = new MyApplication();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        try{
            baseUrl = getIntent().getExtras().getString("baseUrl");
            appViewModel.setBaseUrl(baseUrl);
            appViewModel.init(context, baseUrl);
        }catch (NullPointerException e){}


        appDatabase = AppDatabase.getAppDb(context);

        available_usernames = findViewById(R.id.available_usernames);
        total_games_count = findViewById(R.id.total_games_count);
        network_status_text = findViewById(R.id.network_status_text);
        network_status = findViewById(R.id.network_status);
        progressBar = findViewById(R.id.progressBar);

        schrodingerBtn = findViewById(R.id.schrodingerBtn);
        passwordBtn = findViewById(R.id.passwordBtn);

        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);

        progressBar.setVisibility(View.GONE);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();

        // today's date
        usage = new Usage();
        usage.setId(1);
        usage.setLast_used(dateFormat.format(date));

        mainActivityBL = new MainActivityBL(this,appViewModel, appDatabase, allGamesLeaguesId,allPasswordLeaguesId,allSchrodingerLeaguesId,
                league,allPasswordLeagues,allUniqueLeagues,usage,databaseUsage,mySwipeRefreshLayout,available_usernames,total_games_count,
                network_status_text,schrodingerBtn,passwordBtn,network_status,games, info,context, baseUrl);

        mainActivityBL.updateUsage();
        mainActivityBL.setInfo();
        View rootView = findViewById(R.id.layout_placeholder);
        mainActivityBL.goHome(rootView);
        update();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update(){
        mainActivityBL.update();
    }

    public void setInfo(){
        mainActivityBL.setInfo();
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
            case R.id.database_selector:
                Intent mainActivity = new Intent(this, StartingActivity.class);
                SharedPreferences spRadio = getBaseContext().getSharedPreferences("spRadio",0);
                spRadio.edit().putString("checked_radio", "4").commit();
                startActivity(mainActivity);



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCompletion() {
        mainActivityBL.onCompletion();
    }
    public void goHome(View view){
        mainActivityBL.goHome(view);
    }
    public void openBetActivity(View view) {
        mainActivityBL.openBetActivity(view);
    }
    public void callApi(View view) {
        mainActivityBL.callApi(view);
    }
    public void allGame(View view) {
        mainActivityBL.allGame(view);
    }
    public void schrodinger(View view) {
        mainActivityBL.schrodinger(view);
    }
    public void table(View view) {
        mainActivityBL.table(view);
    }
    public void passwords(View view) {
        mainActivityBL.passwords(view);
    }
    public void search(View view) {
        mainActivityBL.search(view);
    }
    
    public void disableSwipe(){
        mySwipeRefreshLayout.setEnabled(false);
    }
    @Override
    public void onRefresh(){
        menuActivityService = new MainMenuActivityService(baseUrl);
        Intent serviceintent = new Intent (this, MainMenuActivityService.class);
        //startService(serviceintent);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            startService(serviceintent);
            mainActivityBL.onCompletion();
        });
    }

    //disables back pressed button on this activity and all fragments
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        HomeFragment homeFragment = new HomeFragment();
        View rootView = findViewById(R.id.layout_placeholder);
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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
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
    public void onPause() {
        super.onPause();
        //ActiveActivitiesTracker.activityStopped();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
package com.example.game.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.game.R;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.view.fragments.Login;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class StartingActivity extends AppCompatActivity {

    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private Button mainMenuBtn;
    private RadioButton oneTwoOne, season, infinite;
    private RadioGroup radioGroup;
    private ProgressBar progressBar;
    private String baseUrl;
    private ArrayList<League> leagues;

    SharedPreferences spRadio; // class variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_starting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        askNotificationPermission();
        mainMenuBtn = findViewById(R.id.mainMenuBtn);
        oneTwoOne = findViewById(R.id.radio_oneTwoOne);
        season = findViewById(R.id.radio_season);
        infinite = findViewById(R.id.radio_infinite);
        radioGroup = findViewById(R.id.radiogroup);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //radioGroup.check(oneTwoOne.getId());
        spRadio = getBaseContext().getSharedPreferences("spRadio",0);



        mainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(oneTwoOne.isChecked()){
                    saveCheckedRadioButton("1");
                    infinite.setChecked(false);
                    appViewModel = new ViewModelProvider(StartingActivity.this).get(AppViewModel.class);
                    baseUrl = "http://10.0.0.85:3000";
                    appViewModel.init(getBaseContext(), baseUrl);
                    appViewModel.setBaseUrl(baseUrl);
                    appDatabase = AppDatabase.getAppDb(getBaseContext());
                    appDatabase.clearSomeTables();

                    if(appDatabase.getLoggedInUser() == null){
                        Login login = new Login();
                        Bundle bundle = new Bundle();
                        bundle.putString("baseUrl", baseUrl);   //parameters are (key, value).
                        login.setArguments(bundle);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.layout_placeholder,login, login.getClass().getName());
                        ft.commit();
                    }
                    else {

                        appViewModel.getAllLeagues();
                        appViewModel.getUsernameInfo();
                        appViewModel.getTodayGame();
                        appViewModel.getCheckedGames();
                        appViewModel.getSchrodingerGames();
                        appViewModel.getTodayUsername();

                        leagues = (ArrayList<League>) appDatabase.getGamesLeagues();
                        for (League league: leagues) {
                            appViewModel.getStanding(league);
                            appViewModel.getGames(league);
                        }

                        appViewModel.isLoading().observe(StartingActivity.this, new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean aBoolean) {
                                if(aBoolean){
                                    progressBar.setVisibility(View.VISIBLE);
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Intent mainMenuActivityIntent = new Intent(getBaseContext(), MainActivity.class);
                                    mainMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mainMenuActivityIntent.putExtra("baseUrl", baseUrl);
                                    startActivity(mainMenuActivityIntent);
                                    finish();
                                }
                            }
                        });
                    }
                }
                else if(season.isChecked()){
                    saveCheckedRadioButton("2");
                    oneTwoOne.setChecked(false);
                    baseUrl = "http://10.0.0.85:3001";
                    appViewModel = new ViewModelProvider(StartingActivity.this).get(AppViewModel.class);
                    appViewModel.init(getBaseContext(), baseUrl);
                    appViewModel.setBaseUrl(baseUrl);
                    appDatabase = AppDatabase.getAppDb(getBaseContext());

                    appDatabase.clearSomeTables();

                    if(appDatabase.getLoggedInUser() == null){
                        Login login = new Login();
                        Bundle bundle = new Bundle();
                        bundle.putString("baseUrl", baseUrl);   //parameters are (key, value).
                        login.setArguments(bundle);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.layout_placeholder, login, login.getClass().getName());
                        ft.commit();
                    }
                    else {

                        appViewModel.getAllLeagues();
                        appViewModel.getUsernameInfo();
                        appViewModel.getTodayGame();
                        appViewModel.getCheckedGames();
                        appViewModel.getSchrodingerGames();
                        appViewModel.getTodayUsername();

                        leagues = (ArrayList<League>) appDatabase.getGamesLeagues();
                        for (League league: leagues) {
                            appViewModel.getStanding(league);
                            appViewModel.getGames(league);
                        }

                        appViewModel.isLoading().observe(StartingActivity.this, new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean aBoolean) {
                                if(aBoolean){
                                    progressBar.setVisibility(View.VISIBLE);
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Intent mainMenuActivityIntent = new Intent(getBaseContext(), MainActivity.class);
                                    mainMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mainMenuActivityIntent.putExtra("baseUrl", baseUrl);
                                    startActivity(mainMenuActivityIntent);
                                    finish();
                                }
                            }
                        });
                    }
                }
                else if(infinite.isChecked()){
                    saveCheckedRadioButton("3");
                    oneTwoOne.setChecked(false);
                    baseUrl = "http://10.0.0.85:3002";
                    appViewModel = new ViewModelProvider(StartingActivity.this).get(AppViewModel.class);
                    appViewModel.init(getBaseContext(), baseUrl);
                    appViewModel.setBaseUrl(baseUrl);
                    appDatabase = AppDatabase.getAppDb(getBaseContext());

                    appDatabase.clearSomeTables();

                    if(appDatabase.getLoggedInUser() == null){
                        Login login = new Login();
                        Bundle bundle = new Bundle();
                        bundle.putString("baseUrl", baseUrl);   //parameters are (key, value).
                        login.setArguments(bundle);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.layout_placeholder, login, login.getClass().getName());
                        ft.commit();
                    }
                    else {

                        appViewModel.getAllLeagues();
                        appViewModel.getUsernameInfo();
                        appViewModel.getTodayGame();
                        appViewModel.getCheckedGames();
                        appViewModel.getSchrodingerGames();
                        appViewModel.getTodayUsername();

                        leagues = (ArrayList<League>) appDatabase.getGamesLeagues();
                        for (League league: leagues) {
                            appViewModel.getStanding(league);
                            appViewModel.getGames(league);
                        }

                        appViewModel.isLoading().observe(StartingActivity.this, new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean aBoolean) {
                                if(aBoolean){
                                    progressBar.setVisibility(View.VISIBLE);
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Intent mainMenuActivityIntent = new Intent(getBaseContext(), MainActivity.class);
                                    mainMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mainMenuActivityIntent.putExtra("baseUrl", baseUrl);
                                    startActivity(mainMenuActivityIntent);
                                    finish();
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    public void saveCheckedRadioButton(String strRB)
    {
        spRadio.edit().putString("checked_radio", strRB).commit();
    }

    public String getCheckedRadioButton()
    {
        return spRadio.getString("checked_radio", "");
    }
    public void setCheckedRadioButton(){
       SharedPreferences spRadio = getBaseContext().getSharedPreferences("spRadio",0);
       spRadio.edit().putString("checked_radio", "4").commit();
       //saveCheckedRadioButton("4");

    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {

                }
            });
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
            }else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        ActiveActivitiesTracker.activityStarted(this.getBaseContext());
    }
    @Override
    public void onStop() {
        super.onStop();
        appViewModel = null;
        ActiveActivitiesTracker.activityStopped();
    }
    @Override
    public void onPause() {
        super.onPause();
        appViewModel = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        String getChecked = getCheckedRadioButton();

        if (Objects.equals(getChecked, "1")){

            radioGroup.check(oneTwoOne.getId());
            baseUrl = "http://10.0.0.85:3000";
            Intent mainMenuActivityIntent = new Intent(getBaseContext(), MainActivity.class);
            mainMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainMenuActivityIntent.putExtra("baseUrl", baseUrl);
            startActivity(mainMenuActivityIntent);
        }else if(Objects.equals(getChecked, "2")){

            radioGroup.check(season.getId());
            baseUrl = "http://10.0.0.85:3001";
            Intent mainMenuActivityIntent = new Intent(getBaseContext(), MainActivity.class);
            mainMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainMenuActivityIntent.putExtra("baseUrl", baseUrl);
            startActivity(mainMenuActivityIntent);
        }else if(Objects.equals(getChecked, "3")){

            radioGroup.check(infinite.getId());
            baseUrl = "http://10.0.0.85:3002";
            Intent mainMenuActivityIntent = new Intent(getBaseContext(), MainActivity.class);
            mainMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainMenuActivityIntent.putExtra("baseUrl", baseUrl);
            startActivity(mainMenuActivityIntent);
        }
    }
}
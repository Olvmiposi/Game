package com.example.game.view;

import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.view.fragments.Login;
import com.example.game.viewModel.AppViewModel;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    private AppViewModel appViewModel;
    private AppDatabase appDatabase;
    private Button mainMenuBtn;
    private RadioButton oneTwoOne, infinite;
    private RadioGroup radioGroup;
    private ProgressBar progressBar;
    private ArrayList<League> leagues;

    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        baseUrl = "http://10.0.0.85:3002";
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.setBaseUrl(baseUrl);
        appViewModel.init(getBaseContext(), baseUrl);
        appDatabase = AppDatabase.getAppDb(getBaseContext());
        askNotificationPermission();
        mainMenuBtn = findViewById(R.id.mainMenuBtn);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        if(appDatabase.getLoggedInUser() == null){
            Login login = new Login();
            appViewModel.showFragment(login, rootView);

        }
        else {
            Intent mainActivityIntent = new Intent(getBaseContext(), StartingActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivityIntent);
        }
        mainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuActivityIntent = new Intent(getBaseContext(), StartingActivity.class);
                mainMenuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainMenuActivityIntent);
            }
        });
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
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
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
}
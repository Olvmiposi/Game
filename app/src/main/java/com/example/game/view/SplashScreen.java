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
import androidx.fragment.app.FragmentTransaction;
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
    SharedPreferences spRadio; // class variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.init(getBaseContext());
        appDatabase = AppDatabase.getAppDb(getBaseContext());
        askNotificationPermission();
        mainMenuBtn = findViewById(R.id.mainMenuBtn);

        if(appDatabase.getLoggedInUser() == null){
            Login login = new Login();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.layout_placeholder, login, login.getClass().getName());
            ft.commit();

//            try{
//
//                appViewModel.showFragment(login, getCurrentFocus().getRootView());
//
//            }catch (NullPointerException e){
//
//            }


        }
        else {
            Intent mainActivityIntent = new Intent(getBaseContext(), MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivityIntent);
        }
        mainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainMenuActivityIntent = new Intent(getBaseContext(), MainActivity.class);
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
    public void onStart() {
        super.onStart();
        //ActiveActivitiesTracker.activityStarted(this.getBaseContext());
    }
    @Override
    public void onStop() {
        super.onStop();
        appViewModel = null;
        //ActiveActivitiesTracker.activityStopped();
    }
    public void onPause() {
        super.onPause();
        appViewModel = null;
        //ActiveActivitiesTracker.activityStopped();
    }
}
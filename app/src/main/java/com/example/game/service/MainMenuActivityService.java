package com.example.game.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.Nullable;

import com.example.game.helpers.MyReceiver;
import com.example.game.model.Game;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;
import com.example.game.repository.AppRepository;

import java.util.ArrayList;

public class MainMenuActivityService extends IntentService {

    private AppRepository appRepository;
    private MyReceiver receiver;
    private AppDatabase appDatabase;
    private League league;
    private ArrayList<Integer> allGamesLeaguesId,allGamesSeason;

    private ArrayList<Game>  games;


    public MainMenuActivityService() {
        super("MainMenuActivityService");
        appRepository = new AppRepository();
        appRepository.init(getBaseContext());
        appDatabase = AppDatabase.getAppDb(getBaseContext());
    }

    @SuppressLint("WrongThread")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Simulate a long running task by
        // sleeping the thread for 5 seconds
        try {
            Thread.sleep(5000);
            IntentFilter intentFilter = new IntentFilter();

            appRepository.getUsernameInfo();
            appRepository.getTodayGame();
            appRepository.getCheckedGames();
            appRepository.getGames();
            if (appRepository.getGamesLiveData() != null){
                appRepository.getSchrodingerGames();// must be gotten last
            }


        } catch (InterruptedException e) {
            // Print stack trace if an
            // InterruptedException occurs
            e.printStackTrace();
        }
    }
}

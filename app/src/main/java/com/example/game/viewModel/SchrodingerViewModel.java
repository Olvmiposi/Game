package com.example.game.viewModel;

import android.content.Context;
import android.os.Build;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.game.model.Game;
import com.example.game.model.User;
import com.example.game.repository.AppRepository;

import java.util.ArrayList;

public class SchrodingerViewModel extends ViewModel {
    public User user ;
    public Game game ;
    private MutableLiveData<Boolean> isLoading;

    MyApplication myApplication;
    private MutableLiveData<ArrayList<Game>> searchGame, searchSchrodinger, getSchrodingerGames;

    private AppRepository appRepository;
    private SavedStateHandle state;

    public SchrodingerViewModel(SavedStateHandle state) {
        this.state = state;
        appRepository = new AppRepository();
        user = new User();
        game = new Game();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            myApplication = new MyApplication();
        }

        isLoading = appRepository.getIsLoading();
        searchGame = appRepository.getSearchGameLiveData();
        searchSchrodinger = appRepository.getSearchSchrodingerLiveData();
        getSchrodingerGames = appRepository.getSchrodingerGamesLiveData();

    }

    public void init( Context context, String baseUrl) {
        appRepository.init(context, baseUrl);
    }

    public void searchSchrodingerOnClick( String s ) {
        appRepository.searchSchrodinger(s);
    }
    public void getSchrodingerGames(){
        appRepository.getSchrodingerGames();
        appRepository.getSchrodingers();
    }

    public MutableLiveData<ArrayList<Game>> getSchrodingerGamesResponse(){
        if (getSchrodingerGames == null) {
            getSchrodingerGames =  new MutableLiveData<ArrayList<Game>>();
        }
        return getSchrodingerGames;
    }
    public MutableLiveData<ArrayList<Game>> getSearchGameResponse() {
        if (searchGame == null) {
            searchGame =  new MutableLiveData<ArrayList<Game>>();
        }
        return searchGame;
    }

    public MutableLiveData<ArrayList<Game>> getSearchSchrodingerResponse() {
        if (searchSchrodinger == null) {
            searchSchrodinger =  new MutableLiveData<ArrayList<Game>>();
        }
        return searchSchrodinger;
    }
}
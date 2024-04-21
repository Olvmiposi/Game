package com.example.game.repository;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.example.game.model.Bet;
import com.example.game.model.BetResponse;
import com.example.game.model.CallApiModel;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.model.Info;
import com.example.game.model.League;
import com.example.game.model.Schrodinger;
import com.example.game.model.Usage;
import com.example.game.model.User;
import com.example.game.response.LoginResponse;
import com.example.game.retrofit.ApiRequest;
import com.example.game.retrofit.RetrofitClient;
import com.example.game.view.MainActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {
    private MutableLiveData<LoginResponse> loginUser;
    private MutableLiveData<Info> getUsernameInfo;
    private MutableLiveData<Game> verifyGame;
    private MutableLiveData<ArrayList<BetResponse>> getPairs;

    private MutableLiveData<Void> callApi, updateLeagueFile;
    private MutableLiveData<ArrayList<League>> getAllLeagues;
    private MutableLiveData<ArrayList<ClubStats>> standing;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<ArrayList<Game>> getUpdate, verifyPastGame, searchGame, searchSchrodinger, createGame, getGames, todayGame, todayUsername, getAllPredictions, getSchrodingerGames;

    private MutableLiveData<ArrayList<Schrodinger>>  getSchrodinger;

    private ApiRequest apiRequest;
    private RetrofitClient retrofitClient;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private AppDatabase appDatabase;
    private Game newGame;
    private User newUser;
    private Info info;
    private League newLeague;

    public AppRepository() {
        newGame = new Game();
        newUser = new User();
        loginUser = new MutableLiveData<>();
        callApi = new MutableLiveData<>();
        verifyPastGame = new MutableLiveData<>();
        createGame = new MutableLiveData<>();
        getUpdate = new MutableLiveData<>();
        updateLeagueFile = new MutableLiveData<>();
        getAllLeagues = new MutableLiveData<>();
        getUsernameInfo = new MutableLiveData<>();
        getAllPredictions = new MutableLiveData<>();
        searchGame = new MutableLiveData<>();
        getSchrodinger = new MutableLiveData<>();
        searchSchrodinger = new MutableLiveData<>();
        getGames = new MutableLiveData<>();
        verifyGame = new MutableLiveData<>();
        todayGame = new MutableLiveData<>();
        todayUsername = new MutableLiveData<>();
        getSchrodingerGames = new MutableLiveData<>();
        getPairs = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
    }
    public void init( Context context) {
        retrofitClient = new RetrofitClient(context);
        apiRequest = retrofitClient.getApiRequest();
        appDatabase = AppDatabase.getAppDb(context);
    }

    public void loginUser(User user, View view) {
        isLoading.postValue(true);
        prefs = view.getContext().getSharedPreferences("myKey", Context.MODE_PRIVATE);
        editor = prefs.edit();
        apiRequest.login(user).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                // get user from Api response and store it in database
                // get league from Api response and store it in database
                // get games from Api response and store it in database
                if (response.isSuccessful()){
                    isLoading.postValue(false);

                    Intent MainActivityIntent = new Intent(view.getContext(), MainActivity.class);
                    MainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(MainActivityIntent);

                    loginUser.postValue(response.body());
                    Toast.makeText(view.getContext(), "Login successful ", Toast.LENGTH_SHORT).show();

                    newUser = response.body().getUser();
                    newUser.setToken(response.body().getToken());
                    appDatabase.addUser(newUser);

                    appDatabase.addLeagueList(response.body().getLeagues());
                    appDatabase.addGameList(response.body().getGames());

                    editor.putBoolean("saveLogin", true);
                    editor.putInt("id", newUser.getId());
                    editor.putString("token", response.body().getToken());
                    editor.apply();


                }


            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                editor.clear();
                editor.commit();
                isLoading.postValue(false);
                Toast.makeText(view.getContext(), "Login Failed " + t.getMessage(), Toast.LENGTH_LONG).show();
                loginUser.postValue(null);
            }
        });
    }

    public void storeLeagues(ArrayList<League> leagues){
        for ( League league: leagues) {
            newLeague = appDatabase.getLeagueById(league.getId());
            if (newLeague != null){
                appDatabase.updateLeague(league);
            }else{
                appDatabase.addLeague(league);
            }
        }
    }

    public void storeGames(ArrayList<Game> games){
        for ( Game game: games) {
            newGame = appDatabase.getGame(game.getId());
            if (newGame != null){
                appDatabase.updateGame(game);
            }else{
                appDatabase.addGame(game);
            }
        }
    }

    public MutableLiveData<Void> callApi(CallApiModel callApiModel, View view) {
        apiRequest.callApi( callApiModel ).enqueue(new Callback <Void>() {
            @Override
            public void onResponse( Call <Void> call, Response <Void> response) {
                if (response.isSuccessful()){
                    callApi.postValue(response.body());
                    Toast.makeText(view.getContext(), "API called successfully", Toast.LENGTH_SHORT).show();
                } else {
                    callApi.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<Void>call, Throwable t) {
                callApi.postValue(null);
            }
        });
        return callApi;
    }

    public MutableLiveData<ArrayList<BetResponse>> getPairs(Bet bet) {
        isLoading.postValue(true);
        apiRequest.getPairs(bet).enqueue(new Callback <ArrayList<BetResponse>>() {
            @Override
            public void onResponse( Call <ArrayList<BetResponse>> call, Response <ArrayList<BetResponse>> response) {
                isLoading.postValue(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()){
                            getPairs.postValue(response.body());
                        } else {
                            getPairs.postValue(null);
                        }
                    }
                }).start();
            }
            @Override
            public void onFailure(Call<ArrayList<BetResponse>>call, Throwable t) {
                isLoading.postValue(false);
                getPairs.postValue(null);
            }
        });
        return getPairs;
    }


    public MutableLiveData<ArrayList<Game>>verifyPastGame(View view) {
        apiRequest.verifyPastGame().enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response<ArrayList<Game>> response) {
                verifyPastGame.postValue(response.body());
                Toast.makeText(view.getContext(), "verified past game successfully", Toast.LENGTH_SHORT).show();

                if(response.isSuccessful()){
                    try {
                        appDatabase.addGameList(response.body());
                        for ( Game games: response.body()) {
                            //appDatabase.updateGame(games);
                            appDatabase.getGamePossibilities(games.getFixtureId()).observe((LifecycleOwner) view.getContext(), games1 -> {
                                for ( Game game: games1) {
                                    appDatabase.removeGame(game);
                                }
                            });
                        }
                    }catch (NullPointerException e){}
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Game>>call, Throwable t) {
                verifyPastGame.postValue(null);
            }
        });
        return verifyPastGame;
    }
    public MutableLiveData<ArrayList<ClubStats>>getStandings(League league) {
        apiRequest.getStandings(league).enqueue(new Callback<ArrayList<ClubStats>>() {
            @Override
            public void onResponse(Call<ArrayList<ClubStats>> call, Response<ArrayList<ClubStats>> response) {
                appDatabase.addAllToTable(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<ClubStats>> call, Throwable t) {

            }
        });
        return standing;
    }

    public MutableLiveData<ArrayList<Game>>getUpdate( Usage usage) {
        apiRequest.getUpdates(usage).enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response<ArrayList<Game>> response) {
                getUpdate.postValue(response.body());
                if(response.isSuccessful()){

                    try {
                        appDatabase.addGameList(response.body());
                        //storeGames(response.body());
                    }catch (NullPointerException e){}
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Game>>call, Throwable t) {
                getUpdate.postValue(null);
            }
        });
        return getUpdate;
    }

    public MutableLiveData<ArrayList<Game>> createGame(View view) {
        apiRequest.createGame().enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response <ArrayList<Game>> response) {

                createGame.postValue(response.body());
                Toast.makeText(view.getContext(), "Created Games successful", Toast.LENGTH_SHORT).show();

                if(response.isSuccessful()){
                    try{
                        appDatabase.addGameList(response.body());
//                        for ( Game games: response.body()) {
//                            newGame = appDatabase.getGame(games.getId());
//                            if (newGame != null){
//                                appDatabase.updateGame(games);
//                            }else{
//                                appDatabase.addGame(games);
//                            }
//                        }

                    }catch (NullPointerException e){}
                }
            }
            @Override
            public void onFailure(Call<ArrayList <Game> >call, Throwable t) {
                createGame.postValue(null);
            }
        });
        return createGame;
    }

    public MutableLiveData<ArrayList<Game>> searchGame(String query) {
        isLoading.postValue(true);
        apiRequest.searchGame(query).enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response <ArrayList<Game>> response) {
                isLoading.postValue(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()){
                            searchGame.postValue(response.body());
                        } else {
                            searchGame.postValue(null);
                        }
                    }
                }).start();
            }
            @Override
            public void onFailure(Call<ArrayList <Game> >call, Throwable t) {
                isLoading.postValue(false);
                searchGame.postValue(null);
            }
        });
        return searchGame;
    }

    public MutableLiveData<Info> getUsernameInfo() {

        apiRequest.getUsernameInfo().enqueue(new Callback<Info>() {
            @Override
            public void onResponse(Call<Info> call, Response<Info> response) {
                getUsernameInfo.postValue(response.body());
                try {
                    info = appDatabase.getInfo(1);
                    if (info == null){
                        info = response.body();
                        info.setId(1);
                        appDatabase.addInfo(info);
                    }else{
                        info = response.body();
                        info.setId(1);
                        appDatabase.updateInfo(info);
                    }
                }catch (NullPointerException e){}
            }
            @Override
            public void onFailure(Call<Info> call, Throwable t) {
                getUsernameInfo.postValue(null);
            }
        });
        return getUsernameInfo;
    }

    public MutableLiveData<ArrayList<Game>> searchSchrodinger(String query) {
        isLoading.postValue(true);
        apiRequest.searchSchrodinger(query).enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response <ArrayList<Game>> response) {
                isLoading.postValue(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()){
                            searchSchrodinger.postValue(response.body());
                        } else {
                            searchSchrodinger.postValue(null);
                        }
                    }
                }).start();
            }
            @Override
            public void onFailure(Call<ArrayList <Game> >call, Throwable t) {
                isLoading.postValue(false);
                searchSchrodinger.postValue(null);
            }
        });
        return searchSchrodinger;
    }
    public MutableLiveData<ArrayList<Game>> getGames() {
        isLoading.postValue(true);
        apiRequest.getGames().enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response <ArrayList<Game>> response) {
                isLoading.postValue(false);
                getGames.postValue(response.body());

                if (response.isSuccessful()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                appDatabase.addGameList(response.body());

//                                for ( Game games: response.body()) {
//                                    newGame = appDatabase.getGame(games.getId());
//                                    if (newGame != null){
//                                        appDatabase.updateGame(games);
//                                    }else{
//                                        appDatabase.addGame(games);
//                                    }
//                                }
                            }catch (NullPointerException e){}
                        }
                    }).start();
                }
            }
            @Override
            public void onFailure(Call<ArrayList <Game> >call, Throwable t) {
                isLoading.postValue(false);
                getGames.postValue(null);
            }
        });
        return getGames;
    }

    public MutableLiveData<Void> updateLeagueFile( View view) {
        apiRequest.updateLeagueFile().enqueue(new Callback <Void>() {
            @Override
            public void onResponse( Call <Void> call, Response <Void> response) {

                updateLeagueFile.postValue(null);
                Toast.makeText(view.getContext(), "League File Updated Successfully", Toast.LENGTH_SHORT).show();

                if(response.isSuccessful()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                getAllLeagues();
                            }catch (NullPointerException e){}
                        }
                    }).start();
                }
            }
            @Override
            public void onFailure(Call<Void>call, Throwable t) {
                updateLeagueFile.postValue(null);
            }
        });
        return updateLeagueFile;
    }

    public MutableLiveData<ArrayList<League>> getAllLeagues() {
        apiRequest.getAllLeagues().enqueue(new Callback <ArrayList<League>> () {
            @Override
            public void onResponse( Call <ArrayList<League>>  call, Response <ArrayList<League>>  response) {
                getAllLeagues.postValue(response.body());

                if(response.isSuccessful()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                appDatabase.addLeagueList(response.body());


                            }catch (NullPointerException e){}
                        }
                    }).start();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<League>> call, Throwable t) {
                getAllLeagues.postValue(null);
            }
        });
        return getAllLeagues;
    }

    public MutableLiveData<ArrayList<Game>> getTodayGame() {
        isLoading.postValue(true);
        apiRequest.getTodayGame().enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response <ArrayList<Game>> response) {
                isLoading.postValue(false);
                todayGame.postValue(response.body());
            }
            @Override
            public void onFailure(Call<ArrayList <Game> >call, Throwable t) {
                isLoading.postValue(false);
                todayGame.postValue(null);
            }
        });
        return todayGame;
    }

    public MutableLiveData<ArrayList<Game>> getCheckedGames() {
        apiRequest.getCheckedGames().enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response <ArrayList<Game>> response) {
                todayUsername.postValue(response.body());
                if(response.isSuccessful()){
                    try {
                        appDatabase.addGameList(response.body());
                    }catch (NullPointerException e){}
                }
            }
            @Override
            public void onFailure(Call<ArrayList <Game> >call, Throwable t) {
                todayUsername.postValue(null);
            }
        });
        return todayUsername;
    }

    public MutableLiveData<ArrayList<Game>> getSchrodingerGames() {
        isLoading.postValue(true);
        apiRequest.getSchrodingerGames().enqueue(new Callback <ArrayList<Game>>() {
            @Override
            public void onResponse( Call <ArrayList<Game>> call, Response <ArrayList<Game>> response) {
                isLoading.postValue(false);
                getSchrodingerGames.postValue(response.body());

                if(response.isSuccessful()){
                    try {
                        for ( Game games: response.body()) {
                            newGame = appDatabase.getGame(games.getId());
                            games.setSchrodinger(1);
                            if (newGame != null){
                                appDatabase.updateGame(games);
                            }else{
                                appDatabase.addGame(games);
                            }
                        }
                    }catch (NullPointerException e){}
                }
            }
            @Override
            public void onFailure(Call<ArrayList <Game> >call, Throwable t) {
                isLoading.postValue(false);
                getSchrodingerGames.postValue(null);
            }
        });
        return getSchrodingerGames;
    }


    //if api is returning schrodinger info
//    public MutableLiveData<ArrayList<Game>> getSchrodingerGames() {
//        isLoading.postValue(true);
//        apiRequest.getSchrodingerGames().enqueue(new Callback <ArrayList<Schrodinger>>() {
//            @Override
//            public void onResponse( Call <ArrayList<Schrodinger>> call, Response <ArrayList<Schrodinger>> response) {
//                isLoading.postValue(false);
//                getSchrodinger.postValue(response.body());
//                if(response.isSuccessful()){
//                    try {
//                        appDatabase.addAllSchrodinger(response.body());
//                        getSchrodingerGames.postValue(appDatabase.getSchrodingersGames());
//                    }catch (NullPointerException e){}
//                }
//            }
//            @Override
//            public void onFailure(Call<ArrayList <Schrodinger> >call, Throwable t) {
//                isLoading.postValue(false);
//                getSchrodingerGames.postValue(null);
//            }
//        });
//        return getSchrodingerGames;
//    }

    public MutableLiveData<Game>verifyGame(int gameId, Game game, View view) {
        apiRequest.verifyGame(gameId, game).enqueue(new Callback <Game>() {
            @Override
            public void onResponse( Call <Game> call, Response <Game> response) {

                if(response.isSuccessful()){
                    try {
                        appDatabase.updateGame(game);
                    }catch (NullPointerException e){}
                }

                verifyGame.postValue(response.body());
                Toast.makeText(view.getContext(), "Successful", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call <Game >call, Throwable t) {
                verifyGame.postValue(null);
            }
        });
        return verifyGame;
    }
    public MutableLiveData<ArrayList<Game>> getCreateGame() {
        return createGame;
    }
    public MutableLiveData<Void> getUpdateLeagueFile() {
        return updateLeagueFile;
    }
    public MutableLiveData<ArrayList<League>> getGetAllLeagues() {
        return getAllLeagues;
    }
    public MutableLiveData<ArrayList<Game>> getVerifyPastGame() {
        return verifyPastGame;
    }
    public MutableLiveData<LoginResponse> getLoginUserLiveData() {
        return loginUser;
    }
    public MutableLiveData<ArrayList<ClubStats>> getStandingLiveData() {
        return standing;
    }
    public MutableLiveData<Void> getCallApiLiveData() {
        return callApi;
    }
    public MutableLiveData<ArrayList<Game>> getSearchGameLiveData() {
        return searchGame;
    }
    public MutableLiveData<ArrayList<Game>> getSearchSchrodingerLiveData() {
        return searchSchrodinger;
    }
    public MutableLiveData<ArrayList<Game>> getSchrodingerGamesLiveData() {
        return getSchrodingerGames;
    }
    public MutableLiveData<ArrayList<Game>> getTodayGameLiveData() {
        return todayGame;
    }
    public MutableLiveData<ArrayList<Game>> getTodayUsernameLiveData() {
        return todayUsername;
    }
    public MutableLiveData<ArrayList<Game>> getGamesLiveData() {
        return getGames;
    }
    public MutableLiveData<Info> getUsernameInfoLiveData() {
        return getUsernameInfo;
    }
    public MutableLiveData<ArrayList<Game>> getAllPredictionsLiveData() {
        return getAllPredictions;
    }
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public MutableLiveData<ArrayList<BetResponse>> getGetPairs() {
        return getPairs;
    }
    public MutableLiveData<ArrayList<Game>> getGetUpdate() {
        return getUpdate;
    }
}
package com.example.game.viewModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.game.R;
import com.example.game.model.Bet;
import com.example.game.model.BetResponse;
import com.example.game.model.CallApiModel;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.model.Info;
import com.example.game.model.League;
import com.example.game.model.Usage;
import com.example.game.model.User;
import com.example.game.repository.AppRepository;
import com.example.game.response.LoginResponse;
import com.example.game.view.MainActivity;

import java.util.ArrayList;

public class AppViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    public Context context;
    public Activity activity;
    public User user ;
    public Game game ;
    private MyApplication myApplication;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<ArrayList<Game>> getUpdate, verifyPastGame, searchGame, searchSchrodinger,createGame, getGames, todayGame, getAllPredictions, todayUsername, getSchrodingerGames;
    private MutableLiveData<LoginResponse> loginUser;
    private MutableLiveData<ArrayList<BetResponse>> getPairs;
    private MutableLiveData<Info> getUsernameInfo;
    private ClubStats table;
    private MutableLiveData<ArrayList<ClubStats>> standing;
    private MutableLiveData<Void> callApi, updateLeagueFile;
    private MutableLiveData<ArrayList<League>>   getAllLeagues;
    private AppRepository appRepository;
    private  SavedStateHandle state;

    public MutableLiveData<String> textView ;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public AppViewModel(SavedStateHandle state) {
        this.state = state;
        textView = new MutableLiveData<String>();
        appRepository = new AppRepository();
        user = new User();
        game = new Game();

        myApplication = new MyApplication();

        isLoading = appRepository.getIsLoading();
        getUpdate = appRepository.getGetUpdate();

        loginUser = appRepository.getLoginUserLiveData();
        standing = appRepository.getStandingLiveData();

        getPairs = appRepository.getGetPairs();
        callApi = appRepository.getCallApiLiveData();
        verifyPastGame = appRepository.getVerifyPastGame();
        createGame = appRepository.getCreateGame();
        updateLeagueFile = appRepository.getUpdateLeagueFile();
        getAllLeagues = appRepository.getGetAllLeagues();

        getUsernameInfo = appRepository.getUsernameInfoLiveData();
        getAllPredictions = appRepository.getAllPredictionsLiveData();

        searchGame = appRepository.getSearchGameLiveData();
        searchSchrodinger = appRepository.getSearchSchrodingerLiveData();
        getGames = appRepository.getGamesLiveData();
        todayGame = appRepository.getTodayGameLiveData();
        todayUsername = appRepository.getTodayUsernameLiveData();
        getSchrodingerGames = appRepository.getSchrodingerGamesLiveData();
    }
    public void init( Context context/*, Activity activity*/) {
        this.context = context;
        this.activity = activity;
        appRepository.init(context);
    }
    public MutableLiveData<String> getString() {
        if (textView == null) {
            textView =  new MutableLiveData<> ();
        }
        return textView;
    }
    public void setText(String text) {
        textView.setValue(text);
    }
    public static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) return getActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }
    public Fragment getTopFragment(View view) {
        if (((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String fragmentTag = ((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().getBackStackEntryAt(((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return ((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().findFragmentByTag(fragmentTag);
    }

    public void addFragment(Fragment fragment, View view){
        Fragment topFragment = getTopFragment(view);
        String TAG = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.layout_placeholder, fragment, TAG);


        if (topFragment != null){
            fragmentTransaction.hide(topFragment);
        }

        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    public void showFragment(Fragment fragment, View view) {
        Fragment topFragment = getTopFragment(view);
        String TAG = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.layout_placeholder, fragment, TAG);

        if (topFragment != null){
            if (topFragment.getClass().getSimpleName().equals(TAG)){
                fragmentTransaction.addToBackStack(null);
                backstackFragment(view);
            }else{
                fragmentTransaction.addToBackStack(TAG);
            }
        }else{
            fragmentTransaction.addToBackStack(TAG);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void backstackFragment(View view) {
        //Log.d("Stack count", ((AppCompatActivity) getActivity(context)).getSupportFragmentManager().getBackStackEntryCount() + "");
        if (((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().getBackStackEntryCount() == 0) {
            ((AppCompatActivity) getActivity(view.getContext())).finish();

            Intent myIntent = new Intent(getActivity(view.getContext()), MainActivity.class);
            ((AppCompatActivity) getActivity(view.getContext())).startActivity(myIntent);
        }
        ((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().popBackStack();
        removeCurrentFragment(view);
    }

    private void removeCurrentFragment(View view) {
        FragmentTransaction transaction = ((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager().beginTransaction();
        Fragment currentFrag = ((AppCompatActivity) getActivity(view.getContext())).getSupportFragmentManager()
                .findFragmentById(R.id.layout_placeholder);
        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commitAllowingStateLoss();
    }

    public void loginOnClick(View view) {
        appRepository.loginUser(user, view);
    }
    public void callApi(CallApiModel callApiModel, View view) {
        appRepository.callApi(callApiModel, view);
    }
    public void verifyPastGame(View view){
        appRepository.verifyPastGame(view);
    }
    public void createGame(View view){
        appRepository.createGame(view);
    }
    public void updateLeagueFile(View view){
        appRepository.updateLeagueFile( view);
    }
    public void getAllLeagues(){
        appRepository.getAllLeagues();
    }
    public void verifyGame(View view, Game game, int gameId){
        appRepository.verifyGame(gameId, game, view);
    }
    public void getStanding(League league){
        appRepository.getStandings(league);
    }
    public void getPairs( Bet bet ) {
        appRepository.getPairs(bet);
    }
    public void getUpdate( Usage usage){
        appRepository.getUpdate(usage);
    }
    public void getGames(){
        appRepository.getGames();
    }
    public void getTodayGame(){
        appRepository.getTodayGame();
    }
    public void getTodayUsername(){
        appRepository.getCheckedGames();
    }
    public void searchGameOnClick( String s ) {
        appRepository.searchGame(s);
    }
    public void searchSchrodingerOnClick( String s ) {
        appRepository.searchSchrodinger(s);
    }
    public void getSchrodingerGames(){
        appRepository.getSchrodingerGames();

    }
    public void getUsernameInfo() {
        appRepository.getUsernameInfo();
    }
    public MutableLiveData<Boolean> isLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
        }
        return isLoading;
    }

    public MutableLiveData<ArrayList<Game>> getCheckedGames() {
        if (todayUsername == null) {
            todayUsername = new MutableLiveData<>();
        }
        return todayUsername;
    }

    public MutableLiveData<ArrayList<Game>> getUpdate() {
        if (getUpdate == null) {
            getUpdate = new MutableLiveData<>();
        }
        return getUpdate;
    }
    public MutableLiveData<LoginResponse> loginUser() {
        if (loginUser == null) {
            loginUser = new MutableLiveData<>();
        }
        return loginUser;
    }
    public MutableLiveData<Void> callApiResponse(){
        if (callApi == null) {
            callApi =  new MutableLiveData<Void>();
        }
        return callApi;
    }
    public MutableLiveData<ArrayList<Game>> verifyPastGameResponse(){
        if (verifyPastGame == null) {
            verifyPastGame =  new MutableLiveData<ArrayList<Game>>();
        }
        return verifyPastGame;
    }
    public MutableLiveData<ArrayList<Game>> createGameResponse(){
        if (createGame == null) {
            createGame =  new MutableLiveData<ArrayList<Game>>();
        }
        return createGame;
    }
    public MutableLiveData<Void> updateLeagueFileResponse(){
        if (updateLeagueFile == null) {
            updateLeagueFile =  new MutableLiveData<Void>();
        }
        return updateLeagueFile;
    }
    public MutableLiveData<ArrayList<Game>> todayGameResponse(){
        if (todayGame == null) {
            todayGame =  new MutableLiveData<ArrayList<Game>>();
        }
        return todayGame;
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
    public MutableLiveData<ArrayList<BetResponse>> getPairsResponse(){
        if (getPairs == null) {
            getPairs =  new MutableLiveData<ArrayList<BetResponse>>();
        }
        return getPairs;
    }
    public MutableLiveData<ArrayList<Game>> getSearchSchrodingerResponse() {
        if (searchSchrodinger == null) {
            searchSchrodinger =  new MutableLiveData<ArrayList<Game>>();
        }
        return searchSchrodinger;
    }
    public MutableLiveData<Info> getUsernameInfoResponse() {
        if (getUsernameInfo == null) {
            getUsernameInfo =  new MutableLiveData<Info>();
        }
        return getUsernameInfo;
    }
}
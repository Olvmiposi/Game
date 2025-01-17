package com.example.game.retrofit;

import com.example.game.model.Bet;
import com.example.game.model.BetResponse;
import com.example.game.model.CallApiModel;
import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.model.Info;
import com.example.game.model.League;
import com.example.game.model.LiveUpdate;
import com.example.game.model.LiveUpdateResponse;
import com.example.game.model.Schrodinger;
import com.example.game.model.Token;
import com.example.game.model.Usage;
import com.example.game.model.User;
import com.example.game.response.ApiResponse;
import com.example.game.response.LoginResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRequest   {
    @POST("users/authenticate")
    Call<LoginResponse> login(@Body User user);
    @POST("users/callApi")
    Call<Void> callApi(@Body CallApiModel callApiModel);
    @POST("users/getStandings")
    Call<ArrayList<ClubStats>> getStandings(@Body League league);
    @GET("users/verifyPastGame")
    Call<ArrayList<Game>> verifyPastGame();
    @POST("users/createGame")
    Call<ArrayList<Game>> createGame();
    @POST("users/updateLeagueFile")
    Call<Void> updateLeagueFile();
    @GET("users/getAllLeagues")
    Call<ArrayList<League>> getAllLeagues();
    @GET("users/getUsernameInfo")
    Call<Info> getUsernameInfo();
    @POST("users/getPairs")
    Call<ArrayList<BetResponse>> getPairs(@Body Bet bet );
    @GET("users/search")
    Call<ArrayList<Game>> searchGame(@Query("username") String query );

    @POST("users/updateCheckedGames")
    Call<ArrayList<Game>> updateCheckedGames(@Body League league);

    @POST("users/getLiveUpdate")
    Call<ArrayList<LiveUpdateResponse>> getLiveUpdate(@Body LiveUpdate liveUpdate);

    //@Streaming
    @POST("users/game")
    Call<ArrayList<Game>> getGames(@Body League league);
    @PUT("users/updategame/{id}")
    Call<Game> verifyGame(@Path("id") int gameId, @Body Game game);
    @PUT("users/updateSchrodinger/{id}")
    Call<Game> updateSchrodinger(@Path("id") int gameId, @Body Game game);

    @GET("users/getTodayGame")
    Call<ArrayList<Game>> getTodayGame();
    @GET("users/getCheckedGames")
    Call<ArrayList<Game>> getCheckedGames();
    @GET("users/searchSchrodinger")
    Call<ArrayList<Game>> searchSchrodinger(@Query("username") String query );
    @GET("users/getSchrodingers")
    Call<ArrayList<Schrodinger>> getSchrodingers();
    @GET("users/getSchrodingerGames")
    Call<ArrayList<Game>> getSchrodingerGames();

    //if api is returning schrodinger info
//    @GET("users/getSchrodingerGames")
//    Call<ArrayList<Schrodinger>> getSchrodingerGames();
    @POST("users/getupdate")
    Call<ArrayList<Game>> getUpdates(@Body Usage usage);
    @POST("users/fcmtoken")
    Call<ApiResponse> sendFcmToken(@Body Token token);
}
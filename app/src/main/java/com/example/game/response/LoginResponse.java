package com.example.game.response;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.example.game.model.Game;
import com.example.game.model.League;
import com.example.game.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LoginResponse {

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("user")
    @Expose
    @ColumnInfo
    @PrimaryKey
    private User user;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("token")
    @Expose
    @ColumnInfo
    @PrimaryKey
    private String token;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("message")
    @Expose
    @ColumnInfo
    @PrimaryKey
    private String message;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("games")
    @Expose
    @ColumnInfo
    @PrimaryKey
    private ArrayList<Game> games;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("leagues")
    @Expose
    @ColumnInfo
    @PrimaryKey
    private ArrayList<League> leagues;

    private LoginResponse(){

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    public ArrayList<League> getLeagues() {
        return leagues;
    }

    public void setLeagues(ArrayList<League> leagues) {
        this.leagues = leagues;
    }
}

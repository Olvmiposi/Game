package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BetResponse {

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("games")
    @Expose
    @ColumnInfo
    @PrimaryKey

    private ArrayList<Game> games;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @SerializedName("totalScores")
    @Expose
    private int totalScores;

    private BetResponse(){

    }

    public int getTotalScores() {
        return totalScores;
    }

    public void setTotalScores(int totalScores) {
        this.totalScores = totalScores;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }
}

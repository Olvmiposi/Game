package com.example.game.model;

import androidx.room.ColumnInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Bet implements Serializable {

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Expose
    @SerializedName("date")
    @ColumnInfo
    private String date;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("no_games")
    @ColumnInfo
    public int no_games;

    public Bet (){
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNo_games() {
        return no_games;
    }
    public void setNo_games(int no_games) {
        this.no_games = no_games;
    }
}

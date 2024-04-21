package com.example.game.model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class CallApiModel {
    @ColumnInfo
    @SerializedName("league")
    private int league;
    @ColumnInfo
    @SerializedName("from")
    private String from;

    @ColumnInfo
    @SerializedName("season")
    private int season;

    @ColumnInfo
    @SerializedName("to")
    private String to;
    public CallApiModel() {
    }
    public int getLeague() {
        return league;
    }
    public void setLeague(int league) {
        this.league = league;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

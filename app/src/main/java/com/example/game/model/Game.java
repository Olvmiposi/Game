package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(tableName = "games")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game implements Serializable {

    //@Ignore
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Expose
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    public int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("username")
    @ColumnInfo
    public String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("gender")
    @ColumnInfo
    public String gender;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("gameType")
    @ColumnInfo
    public String gameType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("division")
    @ColumnInfo
    public String division;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("home")
    @ColumnInfo
    public String home;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("away")
    @ColumnInfo
    public String away;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("score1")
    @ColumnInfo
    public String score1;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("odds")
    @ColumnInfo
    public String odds;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("score2")
    @ColumnInfo
    public String score2;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("date")
    @ColumnInfo
    public String date;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("time")
    @ColumnInfo
    public String time;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("checked")
    @ColumnInfo
    public int checked;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("season")
    @ColumnInfo
    public int season;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("schrodinger")
    @ColumnInfo
    public int schrodinger;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("fixtureId")
    @ColumnInfo
    public int fixtureId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("leagueId")
    @ColumnInfo
    public int leagueId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("username_id")
    @ColumnInfo
    public int username_id;

    public Game() {

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getAway() {
        return away;
    }

    public void setAway(String away) {
        this.away = away;
    }

    public String getScore1() {
        return score1;
    }

    public void setScore1(String score1) {
        this.score1 = score1;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public String getScore2() {
        return score2;
    }

    public void setScore2(String score2) {
        this.score2 = score2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public int getSchrodinger() {
        return schrodinger;
    }

    public void setSchrodinger(int schrodinger) {
        this.schrodinger = schrodinger;
    }

    public int getFixtureId() {
        return fixtureId;
    }

    public void setFixtureId(int fixtureId) {
        this.fixtureId = fixtureId;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }
    public int getUsername_id() {
        return username_id;
    }

    public void setUsername_id(int username_id) {
        this.username_id = username_id;
    }
}

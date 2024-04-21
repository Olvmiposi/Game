package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "clubStats")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClubStats {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    @Expose
    @ColumnInfo
    private int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("leagueId")
    @Expose
    @ColumnInfo
    private String leagueId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("leagueName")
    @Expose
    @ColumnInfo
    private String leagueName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("position")
    @Expose
    @ColumnInfo
    private int position;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("name")
    @Expose
    @ColumnInfo
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("allPlayed")
    @Expose
    @ColumnInfo
    private int allPlayed;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("points")
    @Expose
    @ColumnInfo
    private int points;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("win")
    @Expose
    @ColumnInfo
    private int win;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("draw")
    @Expose
    @ColumnInfo
    private int draw;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("lose")
    @Expose
    @ColumnInfo
    private int lose;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("goalsFor")
    @Expose
    @ColumnInfo
    private int goalsFor;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("goalsAgainst")
    @Expose
    @ColumnInfo
    private int goalsAgainst;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("goalsDiff")
    @Expose
    @ColumnInfo
    private int goalsDiff;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("dateTime")
    @Expose
    @ColumnInfo
    private String dateTime;


    public ClubStats(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getAllPlayed() {
        return allPlayed;
    }

    public void setAllPlayed(int allPlayed) {
        this.allPlayed = allPlayed;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getGoalsDiff() {
        return goalsDiff;
    }

    public void setGoalsDiff(int goalsDiff) {
        this.goalsDiff = goalsDiff;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

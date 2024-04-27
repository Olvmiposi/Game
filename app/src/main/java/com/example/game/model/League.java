package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(tableName = "leagues")
@JsonIgnoreProperties(ignoreUnknown = true)
public class League implements Serializable {

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @SerializedName("id")
    @Expose
    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    private int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("leagueId")
    @Expose
    @ColumnInfo
    private int leagueId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("name")
    @Expose
    @ColumnInfo
    private String name;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("season")
    @Expose
    @ColumnInfo
    private int season;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("start")
    @Expose
    @ColumnInfo
    private String start;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("end")
    @Expose
    @ColumnInfo
    private String end;

    public League() {

    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(obj instanceof League)
        {
            League temp = (League) obj;
            if(this.leagueId == temp.leagueId && this.season == temp.season && this.start.equals(temp.start) && this.end.equals(temp.end) && this.name.equals(temp.name))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub

        return (this.name.hashCode() + this.start.hashCode() + this.end.hashCode() + this.name.hashCode());
    }
}

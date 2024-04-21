package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Info implements Serializable {
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("id")
    @Expose
    @ColumnInfo
    @PrimaryKey
    private int id;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("total_usernames")
    @Expose
    @ColumnInfo
    private int total_usernames;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("available_username")
    @Expose
    @ColumnInfo
    private int available_username;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("used_usernames")
    @Expose
    @ColumnInfo
    private int used_usernames;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("no_of_predictions")
    @Expose
    @ColumnInfo
    private int no_of_predictions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("no_of_games")
    @Expose
    @ColumnInfo
    private int no_of_games;

    public Info() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotal_usernames() {
        return total_usernames;
    }

    public void setTotal_usernames(int total_usernames) {
        this.total_usernames = total_usernames;
    }

    public int getAvailable_username() {
        return available_username;
    }

    public void setAvailable_username(int available_username) {
        this.available_username = available_username;
    }
    public int getUsed_usernames() {
        return used_usernames;
    }

    public void setUsed_usernames(int used_usernames) {
        this.used_usernames = used_usernames;
    }

    public int getNo_of_predictions() {
        return no_of_predictions;
    }

    public void setNo_of_predictions(int no_of_predictions) {
        this.no_of_predictions = no_of_predictions;
    }
    public int getNo_of_games() {
        return no_of_games;
    }

    public void setNo_of_games(int no_of_games) {
        this.no_of_games = no_of_games;
    }
}

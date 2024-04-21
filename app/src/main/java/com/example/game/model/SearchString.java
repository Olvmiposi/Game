package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "strings")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchString {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    @Expose
    @ColumnInfo
    private int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("username")
    @Expose
    @ColumnInfo
    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("date")
    @Expose
    @ColumnInfo

    private String date;


    public SearchString(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

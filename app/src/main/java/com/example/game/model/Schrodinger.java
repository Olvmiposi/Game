package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "schrodinger")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schrodinger {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("id")
    @PrimaryKey() //autoGenerate = false
    @Expose
    @ColumnInfo
    private int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("username_id")
    @Expose
    @ColumnInfo
    private int username_id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("user_id")
    @Expose
    @ColumnInfo

    private int user_id;


    public Schrodinger(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsername_id() {
        return username_id;
    }

    public void setUsername_id(int username_id) {
        this.username_id = username_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}

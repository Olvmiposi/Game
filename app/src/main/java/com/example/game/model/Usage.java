package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "usage")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usage {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("id")
    @Expose
    @ColumnInfo
    @PrimaryKey
    private int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("last_used")
    @Expose
    @ColumnInfo
    private String last_used;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @SerializedName("date")
//    @Expose
//    @ColumnInfo
//    private Date date;


    public Usage(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLast_used() {
        return last_used;
    }

    public void setLast_used(String last_used) {
        this.last_used = last_used;
    }
}

package com.example.game.model;

import androidx.room.ColumnInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)

public class LiveUpdate {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("ids")
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}

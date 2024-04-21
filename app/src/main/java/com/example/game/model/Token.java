package com.example.game.model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class Token {
    @ColumnInfo
    @SerializedName("tokenType")
    private String tokenType;

    @ColumnInfo
    @SerializedName("token")
    private String token;

    public String getTokenType() {
        return tokenType;
    }
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}

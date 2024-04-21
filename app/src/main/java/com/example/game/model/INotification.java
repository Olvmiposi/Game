package com.example.game.model;

import com.google.gson.annotations.SerializedName;

public class INotification {
    @SerializedName("type")
    String type;
    @SerializedName("game")
    private Game game;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
}

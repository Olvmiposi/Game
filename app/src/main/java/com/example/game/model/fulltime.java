package com.example.game.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)

public class fulltime {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("score1")
    private String score1;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Expose
    @SerializedName("score2")
    private String score2;

    public String getScore1() {
        return score1;
    }

    public void setScore1(String score1) {
        this.score1 = score1;
    }

    public String getScore2() {
        return score2;
    }

    public void setScore2(String score2) {
        this.score2 = score2;
    }
}

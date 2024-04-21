package com.example.game.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(tableName = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @SerializedName("id")
    @Expose
    @ColumnInfo
    @PrimaryKey
    private int id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("username")
    @Expose
    @ColumnInfo
    private String username;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("password")
    @Expose
    @ColumnInfo
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("otp")
    @Expose
    @ColumnInfo
    private String otp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @SerializedName("token")
    @Expose
    @ColumnInfo
    private String token;

    public User() {

    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }
}

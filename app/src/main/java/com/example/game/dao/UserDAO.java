package com.example.game.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.game.model.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insert(User user);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<User> user);
    @Update
    void update(User user);
    @Query("SELECT * from users ORDER By username Asc")
    LiveData<List<User>> getUsers();
    @Query("SELECT * FROM users WHERE id = :id")
    User getUser(int id);
    @Query("SELECT * FROM users WHERE token = :token ")
    User getLoggedInUser(String token);
    @Query("SELECT * FROM users WHERE token IS NOT NULL ")
    User getLoggedInUser();
    @Delete
    void delete(User user);
}

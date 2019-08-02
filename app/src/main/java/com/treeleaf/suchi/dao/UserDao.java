package com.treeleaf.suchi.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.treeleaf.suchi.realm.models.User;

import java.util.List;

public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM user_table")
    void deleteAllUsers();

    @Query("SELECT * FROM user_table")
    LiveData<List<User>> getAllUsers();
}

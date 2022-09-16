package com.example.eventnotes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventnotes.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM Users")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT " +
            "CASE " +
            "WHEN EXISTS(SELECT userId FROM Users WHERE email = :email) THEN 1 " +
            "ELSE 0 " +
            "END")
    int exists(String email);

    @Insert
    long[] addUser(User... users);

    @Delete
    void removeUser(User... users);

    @Update
    void updateUser(User user);

    @Query("SELECT userId FROM Users WHERE email = :email")
    int getIdFromEmail(String email);
}

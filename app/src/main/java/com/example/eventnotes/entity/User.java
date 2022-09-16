package com.example.eventnotes.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.checkerframework.common.aliasing.qual.Unique;

@Entity(tableName = "Users", indices = {@Index(value = "email", unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long userId;
    @Unique
    public String email;

    public User(@NonNull String email) {
        this.email = email;
    }

    @Ignore
    public User() {
    }
}

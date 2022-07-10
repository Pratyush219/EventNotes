package com.example.eventnotes.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title, subtitle, date, desc, priority;
}

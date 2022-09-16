package com.example.eventnotes.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Notes"
//        indices = {@Index(value = {"userId"})},
//        foreignKeys = @ForeignKey(
//                entity = User.class,
//                parentColumns = "userId",
//                childColumns = "userId",
//                onDelete = CASCADE
//        )
)
public class Note {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String title, subtitle, date, desc, priority;
    public long userId;

    public Note() {
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDate() {
        return date;
    }

    public String getDesc() {
        return desc;
    }

    public String getPriority() {
        return priority;
    }

    public long getUserId() {
        return userId;
    }
}

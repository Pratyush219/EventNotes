package com.example.eventnotes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.eventnotes.dao.NoteDao;
import com.example.eventnotes.model.Note;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    public static NoteDatabase INSTANCE;
    private static final int  MAX_THREADS = 4;
    public static ExecutorService service = Executors.newFixedThreadPool(MAX_THREADS);
    public static NoteDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, NoteDatabase.class, "NotesDatabase").build();
        }
        return INSTANCE;
    }
}

package com.example.eventnotes.database;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.eventnotes.dao.NoteDao;
import com.example.eventnotes.dao.UserDao;
import com.example.eventnotes.entity.Note;
import com.example.eventnotes.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Note.class, User.class},
        version = 1,
        exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    public static NoteDatabase INSTANCE;
    private static final int  MAX_THREADS = 4;
    public static ExecutorService service = Executors.newFixedThreadPool(MAX_THREADS);
    public static NoteDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, NoteDatabase.class, "NotesDatabase")
//                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return INSTANCE;
    }
//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {}
//    };
}

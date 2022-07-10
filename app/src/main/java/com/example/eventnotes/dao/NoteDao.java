package com.example.eventnotes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventnotes.model.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM Notes")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM Notes ORDER BY priority DESC")
    LiveData<List<Note>> priorityDecreasing();

    @Query("SELECT * FROM Notes ORDER BY priority")
    LiveData<List<Note>> priorityIncreasing();

    @Insert
    void addNote(Note... note);

    @Query("DELETE FROM Notes WHERE id = :id")
    void deleteNote(int id);

    @Update
    void update(Note note);


}

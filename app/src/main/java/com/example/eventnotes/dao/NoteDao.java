package com.example.eventnotes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventnotes.entity.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM Notes")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM Notes WHERE userId = :loggedInUserId ORDER BY priority DESC")
    LiveData<List<Note>> priorityDecreasing(long loggedInUserId);

    @Query("SELECT * FROM Notes WHERE userId = :loggedInUserId ORDER BY priority")
    LiveData<List<Note>> priorityIncreasing(long loggedInUserId);

    @Query("SELECT * FROM Notes WHERE id = :id LIMIT 1")
    LiveData<Note> getNoteById(long id);
    @Insert
    long[] addNotes(Note... notes);

    @Query("DELETE FROM Notes WHERE id = :id")
    void deleteNote(long id);

    @Query("SELECT * FROM Notes WHERE userId = :userId")
    LiveData<List<Note>> getNotesForUser(long userId);

    @Update
    void update(Note note);


}

package com.example.eventnotes.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.eventnotes.dao.NoteDao;
import com.example.eventnotes.database.NoteDatabase;
import com.example.eventnotes.model.Note;

import java.util.List;

public class NotesRepository {
    private final NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private LiveData<List<Note>> priorityLowToHigh;
    private LiveData<List<Note>> priorityHighToLow;

    public LiveData<List<Note>> getPriorityLowToHigh() {
        return priorityLowToHigh;
    }

    public LiveData<List<Note>> getPriorityHighToLow() {
        return priorityHighToLow;
    }

    private static NotesRepository mInstance;
    public static NotesRepository getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new NotesRepository(context);
        }
        return mInstance;
    }
    private NotesRepository(Context context) {
        noteDao = NoteDatabase
                .getInstance(context)
                .noteDao();
        allNotes = noteDao.getAllNotes();
        priorityHighToLow = noteDao.priorityDecreasing();
        priorityLowToHigh = noteDao.priorityIncreasing();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void addNote(Note note) {
        NoteDatabase.service.execute(() -> {
            noteDao.addNote(note);
        });
    }

    public void deleteNote(int id) {

        NoteDatabase.service.execute(() -> {
            noteDao.deleteNote(id);
        });
    }

    public void updateNote(Note note) {
        NoteDatabase.service.execute(() -> {
            noteDao.update(note);
        });
    }

}

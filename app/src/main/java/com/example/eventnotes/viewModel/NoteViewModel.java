package com.example.eventnotes.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventnotes.model.Note;
import com.example.eventnotes.repository.NotesRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final NotesRepository repository;
    private final LiveData<List<Note>> allNotes;
    private final LiveData<List<Note>> priorityLowToHigh;
    private final LiveData<List<Note>> priorityHighToLow;

    public LiveData<List<Note>> getPriorityLowToHigh() {
        return priorityLowToHigh;
    }

    public LiveData<List<Note>> getPriorityHighToLow() {
        return priorityHighToLow;
    }

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = NotesRepository.getInstance(application);
        allNotes = repository.getAllNotes();
        priorityHighToLow = repository.getPriorityHighToLow();
        priorityLowToHigh = repository.getPriorityLowToHigh();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void addNote(Note note) {
        repository.addNote(note);
    }

    public void deleteNote(Note note) {
        repository.deleteNote(note.id);
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
    }
}

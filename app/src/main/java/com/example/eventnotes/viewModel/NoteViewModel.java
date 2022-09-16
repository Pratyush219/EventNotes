package com.example.eventnotes.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventnotes.entity.Note;
import com.example.eventnotes.repository.NotesRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final NotesRepository repository;
    private final LiveData<List<Note>> allNotes;
//    private final LiveData<List<Note>> priorityLowToHigh;
//    private final LiveData<List<Note>> priorityHighToLow;
    private final GoogleSignInAccount account;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = NotesRepository.getInstance(application);
        allNotes = repository.getAllNotes();
        account = GoogleSignIn.getLastSignedInAccount(application);
    }

    public LiveData<List<Note>> getPriorityLowToHigh(long loggedInUserId) {
        return repository.getPriorityLowToHigh(loggedInUserId);
    }

    public LiveData<List<Note>> getPriorityHighToLow(long loggedInUserId) {
        return repository.getPriorityHighToLow(loggedInUserId);
    }

    public void loadDataFromCloud(long loggedInUserId) {
        repository.loadDataFromCloud(loggedInUserId);
    }
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<Note> getNoteById(long id) {
        return repository.getNoteById(id);
    }
    public LiveData<List<Note>> getNoteForUser(long userId) {
        return repository.getNotesForUser(userId);
    }

    public void addNote(Note note, boolean userInitiated) {
        repository.addNotes(userInitiated, note);
    }

    public void deleteNote(Note note) {
        repository.deleteNote(note.id);
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
    }
}

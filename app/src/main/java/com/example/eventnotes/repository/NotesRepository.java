package com.example.eventnotes.repository;

import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.LiveData;

import com.example.eventnotes.dao.NoteDao;
import com.example.eventnotes.database.NoteDatabase;
import com.example.eventnotes.entity.Note;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;

public class NotesRepository {
    private static NotesRepository mInstance;
    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;
    private final CollectionReference notesReference;
    private static final String TAG = "NotesRepository";
    private final Handler handler;
//    private final LiveData<List<Note>> priorityLowToHigh;
//    private final LiveData<List<Note>> priorityHighToLow;

    private NotesRepository(Context context) {
        noteDao = NoteDatabase
                .getInstance(context)
                .noteDao();
        allNotes = noteDao.getAllNotes();
//        priorityHighToLow = noteDao.priorityDecreasing(loggedInUserId);
//        priorityLowToHigh = noteDao.priorityIncreasing();
        notesReference = FirebaseFirestore.getInstance().collection("Notes");
        handler = new Handler();
    }

    public static NotesRepository getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NotesRepository(context);
        }
        return mInstance;
    }

    public LiveData<List<Note>> getPriorityLowToHigh(long loggedInUserId) {
        return noteDao.priorityIncreasing(loggedInUserId);
    }

    public LiveData<List<Note>> getPriorityHighToLow(long loggedInUserId) {
        return noteDao.priorityDecreasing(loggedInUserId);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<Note> getNoteById(long id) {
        return noteDao.getNoteById(id);
    }
    public LiveData<List<Note>> getNotesForUser(long userId) {
        return noteDao.getNotesForUser(userId);
    }

    public void addNotes(boolean userInitiated, Note... notes) {
        NoteDatabase.service.execute(() -> handler.post(() -> {
            if(userInitiated) {
                notesReference.orderBy("id", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                long noteId;
                                List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                                if(!snapshots.isEmpty()) {
                                    noteId = 1 + Objects.requireNonNull(snapshots.get(0).toObject(Note.class)).id;
                                    for(Note note: notes) {
                                        note.id = noteId++;
                                    }
                                }

                            }
                        });
            } else {
                noteDao.addNotes(notes);
            }
        }));
    }

    public void deleteNote(long id) {
        NoteDatabase.service.execute(() -> noteDao.deleteNote(id));
    }

    public void loadDataFromCloud(long userId) {
//        List<Note> notes = new ArrayList<>();
        notesReference.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        if (snapshot.exists()) {
                            Note note = snapshot.toObject(Note.class);
//                            notes.add(note);
                            addNotes(false, note);
                        }
                    }
                });
//        addNotes(notes.toArray(new Note[0]));
    }

    public void updateNote(Note note) {
        NoteDatabase.service.execute(() -> {
            noteDao.update(note);
            notesReference.document(String.valueOf(note.id))
                    .set(note);
        });
    }
}

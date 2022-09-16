package com.example.eventnotes.repository;

import com.example.eventnotes.database.NoteDatabase;
import com.example.eventnotes.entity.User;
import com.example.eventnotes.listener.SignInListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class UserRepository {
    private final CollectionReference usersReference;

    public UserRepository() {
        usersReference = FirebaseFirestore.getInstance().collection("Users");
    }
    public void addUser(User user) {
        NoteDatabase.service.execute(() -> usersReference.whereEqualTo("email", user.email)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(task.getResult().getDocuments().isEmpty()) {
                            usersReference.orderBy("userId", Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()) {
                                            for(DocumentSnapshot snapshot1: task1.getResult().getDocuments()) {
                                                if(snapshot1.exists()) {
                                                    user.userId = 1 + Objects.requireNonNull(snapshot1.toObject(User.class)).userId;
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                }));
    }
    public void getIdFromEmail(String email, SignInListener listener) {
        usersReference.whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot snapshot: task.getResult().getDocuments()) {
                            if(snapshot.exists()) {
                                listener.onSignIn(Objects.requireNonNull(snapshot.toObject(User.class)).userId);
                            }
                        }
                    }
                });
    }
//    public LiveData<List<User>> getAllUsers() {
//        return userDao.getAllUsers();
//    }
}

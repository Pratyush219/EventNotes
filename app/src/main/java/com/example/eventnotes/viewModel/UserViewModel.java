package com.example.eventnotes.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.eventnotes.entity.User;
import com.example.eventnotes.listener.SignInListener;
import com.example.eventnotes.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repo;
//    private final LiveData<List<User>> usersList;
    private static final String TAG = "UserViewModel";

    public UserViewModel(@NonNull Application application) {
        super(application);
        repo = new UserRepository();
//        usersList = repo.getAllUsers();
//        SharedPreferences prefs = application.getSharedPreferences("AppPreference", Context.MODE_PRIVATE);
//        if(prefs.getBoolean("firstRun", true)) {
//            Log.i(TAG, "UserViewModel: First launch");
//            getUsersFromCloud();
//            prefs.edit().putBoolean("firstRun", false).apply();
//        } else {
//            Log.i(TAG, "UserViewModel: Repeated launch");
//        }
    }

    public void addUser(User user, SignInListener listener) {
        repo.addUser(user);
    }

    public void setLoggedInUser(String email, SignInListener listener) {
        repo.getIdFromEmail(email, listener);
    }

//    public LiveData<List<User>> getUsersList() {
//        return usersList;
//    }
}

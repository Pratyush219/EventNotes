package com.example.eventnotes.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventnotes.R;
import com.example.eventnotes.entity.User;
import com.example.eventnotes.listener.SignInListener;
import com.example.eventnotes.viewModel.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.util.Set;

public class SignInFragment extends Fragment implements SignInListener {
    private static final int RC_SIGN_IN = 1000;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private NavController navController;
    private UserViewModel model;
    private ProgressBar progressBar;

    private static final String TAG = "SignInFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                .requestServerAuthCode("924188463092-bd6vk8eekjsvgbamntttp6temqb92evj.apps.googleusercontent.com")
                .requestEmail()
                .build();
        model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        progressBar = view.findViewById(R.id.progressBarSignIn);
        signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(v -> {
            if (v.getId() == R.id.sign_in_button) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        if(account != null) {
            Toast.makeText(requireActivity(), "Already signed in: " + account.getEmail(), Toast.LENGTH_SHORT).show();
            updateUI(account);
            Toast.makeText(requireContext(), "Update UI finished", Toast.LENGTH_SHORT).show();
            setLoggedInUser(account);
            Toast.makeText(requireContext(), "Logged in user set", Toast.LENGTH_SHORT).show();
//            navigate();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if(account != null) {
                updateUI(account);
                setLoggedInUser(account);
            } else {
                Toast.makeText(requireActivity(), "ERROR!", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            signInButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            Set<Scope> scopeSet = account.getGrantedScopes();
            StringBuilder scopes = new StringBuilder();
            for (Scope scope : scopeSet) {
                scopes.append(scope.toString()).append(", ");
            }
            Log.w(TAG, "handleSignInResult: Requested scopes=" + scopes);
        }
    }
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.event_menu, menu);
//        menu.findItem(R.id.action_logout).setVisible(false);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.action_to_notes) {
//            navController.navigate(R.id.action_signInFragment_to_notesFragment);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void setLoggedInUser(GoogleSignInAccount account) {
        User user = new User();
        user.email = account.getEmail();
        model.addUser(user, this);
        model.setLoggedInUser(account.getEmail(), this);
    }
    @Override
    public void onSignIn(long userId) {
        if(userId > 0) {
            SharedPreferences preferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("userId", userId);
            editor.putInt(String.valueOf(userId), 1 + preferences.getInt(String.valueOf(userId), 0));
            editor.apply();
            navigate();
        }
    }

    private void navigate() {
        navController.navigate(R.id.action_signInFragment_to_notesFragment);
    }
}

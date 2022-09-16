package com.example.eventnotes.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.eventnotes.R;
import com.example.eventnotes.adapter.NotesAdapter;
import com.example.eventnotes.entity.Note;
import com.example.eventnotes.viewModel.NoteViewModel;
import com.example.eventnotes.viewModel.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment implements NotesAdapter.NoteCLickListener {
    NavController navController;
    FloatingActionButton addNoteBtn;
    RecyclerView notesRecyclerView;
    NoteViewModel viewModel;
    NotesAdapter adapter;
    TextView noFilter, highToLow, lowToHigh;
    List<Note> filteredNotes;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    UserViewModel userViewModel;
    long loggedInUserId;
    private Context context;
    private static final String TAG = "NotesFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                .requestServerAuthCode("924188463092-bd6vk8eekjsvgbamntttp6temqb92evj.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        SharedPreferences preferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        loggedInUserId = preferences.getLong("userId", -1);
//        if(preferences.contains("revisit")) {
//            Toast.makeText(context, preferences.getBoolean("revisit", false) + "", Toast.LENGTH_SHORT).show();
//        }
        Toast.makeText(context, "Revisit = " + preferences.getBoolean("revisit", false), Toast.LENGTH_SHORT).show();

        if(preferences.getInt(String.valueOf(loggedInUserId), 1) <= 1) {
            viewModel.loadDataFromCloud(loggedInUserId);
//            preferences.edit().putBoolean("revisit", true).apply();
            Toast.makeText(context, "Getting data from cloud", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Getting data from local database", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(requireContext(), "Logged in User = " + loggedInUserId, Toast.LENGTH_SHORT).show();
        navController = Navigation.findNavController(view);
        addNoteBtn = view.findViewById(R.id.add_note);
        notesRecyclerView = view.findViewById(R.id.rv_notes);
        noFilter = view.findViewById(R.id.no_filter);
        highToLow = view.findViewById(R.id.filter_high_to_low);
        lowToHigh = view.findViewById(R.id.filter_low_to_high);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        if(loggedInUserId == -1) {
            return;
        }
        noFilter.setBackgroundResource(R.drawable.filter_selected_shape);
        noFilter.setOnClickListener(v -> {
            loadData(0);
            noFilter.setBackgroundResource(R.drawable.filter_selected_shape);
            highToLow.setBackgroundResource(R.drawable.filter_un_shape);
            lowToHigh.setBackgroundResource(R.drawable.filter_un_shape);
        });
        highToLow.setOnClickListener(v -> {
            loadData(1);
            highToLow.setBackgroundResource(R.drawable.filter_selected_shape);
            noFilter.setBackgroundResource(R.drawable.filter_un_shape);
            lowToHigh.setBackgroundResource(R.drawable.filter_un_shape);
        });
        lowToHigh.setOnClickListener(v -> {
            loadData(2);
            lowToHigh.setBackgroundResource(R.drawable.filter_selected_shape);
            highToLow.setBackgroundResource(R.drawable.filter_un_shape);
            noFilter.setBackgroundResource(R.drawable.filter_un_shape);
        });
        addNoteBtn.setOnClickListener(view1 -> navController.navigate(R.id.action_notesFragment_to_addNoteFragment));
        adapter = new NotesAdapter(requireActivity(), this);
        loadData(0);
    }

    private void loadData(int i) {
        if (i == 0) {
            viewModel.getNoteForUser(loggedInUserId).observe(getViewLifecycleOwner(), notes -> {
                setNoteAdapter(notes);
                filteredNotes = notes;
            });
        } else if (i == 1) {
            viewModel.getPriorityHighToLow(loggedInUserId).observe(getViewLifecycleOwner(), notes -> {
                setNoteAdapter(notes);
                filteredNotes = notes;
            });
        } else if (i == 2) {
            viewModel.getPriorityLowToHigh(loggedInUserId).observe(getViewLifecycleOwner(), notes -> {
                setNoteAdapter(notes);
                filteredNotes = notes;
            });
        }
    }

    void setNoteAdapter(List<Note> notes) {
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter.setNotesDisplayed(notes);
        notesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onNoteClick(Note note) {
        Toast.makeText(context, "Inside onNoteClick", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putLong("noteId", note.id);
        bundle.putString("title", note.title);
        bundle.putString("subtitle", note.subtitle);
        bundle.putString("desc", note.desc);
        bundle.putString("date", note.date);
        bundle.putString("priority", note.priority);
        bundle.putLong("userId", note.userId);
        navController.navigate(R.id.action_notesFragment_to_updateNoteFragment, bundle);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_notes, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search notes here...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                notesFilter(s);
                return false;
            }
        });
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        if(account == null) {
            menu.findItem(R.id.action_logout).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_to_event_page) {
            navController.navigate(R.id.action_notesFragment_to_signInFragment);
            return true;
        }
        else if(id == R.id.action_logout) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(requireActivity(), task -> {
                        item.setVisible(false);
                        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.action_notesFragment_to_signInFragment);
                    });
        }
        return super.onOptionsItemSelected(item);
    }

    private void notesFilter(String s) {
        ArrayList<Note> filteredNames = new ArrayList<>();
        for (Note note : filteredNotes) {
            if (note.title.contains(s) || note.subtitle.contains(s)) {
                filteredNames.add(note);
            }
        }
        adapter.searchNotes(filteredNames);
    }
}
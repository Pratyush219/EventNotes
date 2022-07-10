package com.example.eventnotes.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.eventnotes.R;
import com.example.eventnotes.adapter.NotesAdapter;
import com.example.eventnotes.model.Note;
import com.example.eventnotes.viewModel.NoteViewModel;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        addNoteBtn = view.findViewById(R.id.add_note);
        notesRecyclerView = view.findViewById(R.id.rv_notes);
        noFilter = view.findViewById(R.id.no_filter);
        highToLow = view.findViewById(R.id.filter_high_to_low);
        lowToHigh = view.findViewById(R.id.filter_low_to_high);

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
        viewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            setNoteAdapter(notes);
            filteredNotes = notes;
        });
    }

    private void loadData(int i) {
        if (i == 0) {
            viewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
                setNoteAdapter(notes);
                filteredNotes = notes;
            });
        } else if (i == 1) {
            viewModel.getPriorityHighToLow().observe(getViewLifecycleOwner(), notes -> {
                setNoteAdapter(notes);
                filteredNotes = notes;
            });
        } else if (i == 2) {
            viewModel.getPriorityLowToHigh().observe(getViewLifecycleOwner(), notes -> {
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
        Bundle bundle = new Bundle();
        bundle.putInt("noteId", note.id);
        bundle.putString("title", note.title);
        bundle.putString("subtitle", note.subtitle);
        bundle.putString("desc", note.desc);
        bundle.putString("date", note.date);
        bundle.putString("priority", note.priority);
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

        super.onCreateOptionsMenu(menu, inflater);
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
package com.example.eventnotes.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventnotes.R;
import com.example.eventnotes.databinding.FragmentUpdateNoteBinding;
import com.example.eventnotes.model.Note;
import com.example.eventnotes.viewModel.NoteViewModel;
import com.example.eventnotes.R;
import com.example.eventnotes.databinding.FragmentUpdateNoteBinding;
import com.example.eventnotes.model.Note;
import com.example.eventnotes.viewModel.NoteViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class UpdateNoteFragment extends Fragment {
    int noteId;
    NoteViewModel viewModel;
    private String title, subtitle, description, date;
    private String priority = "1";
    private String initPriority;
    FragmentUpdateNoteBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = requireArguments();
        noteId = bundle.getInt("noteId");
        title = bundle.getString("title");
        subtitle = bundle.getString("subtitle");
        description = bundle.getString("desc");
        date = bundle.getString("date");
        initPriority = bundle.getString("priority");
        Toast.makeText(getContext(),  "Note: " + noteId + " " + title + " " + subtitle + " " + date, Toast.LENGTH_SHORT).show();
        viewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        binding = FragmentUpdateNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.etTitle.setText(title);
        binding.etSubtitle.setText(subtitle);
        binding.etDescription.setText(description);

        if(initPriority.equals("1")) {
            binding.priorityLow.setImageResource(R.drawable.ic_done);
            binding.priorityMedium.setImageResource(0);
            binding.priorityHigh.setImageResource(0);
        } else if(initPriority.equals("2")) {
            binding.priorityMedium.setImageResource(R.drawable.ic_done);
            binding.priorityLow.setImageResource(0);
            binding.priorityHigh.setImageResource(0);
        } else {
            binding.priorityHigh.setImageResource(R.drawable.ic_done);
            binding.priorityMedium.setImageResource(0);
            binding.priorityLow.setImageResource(0);
        }

        binding.priorityLow.setOnClickListener(v -> {
            priority = "1";
            binding.priorityLow.setImageResource(R.drawable.ic_done);
            binding.priorityMedium.setImageResource(0);
            binding.priorityHigh.setImageResource(0);
        });
        binding.priorityMedium.setOnClickListener(v -> {
            priority = "2";
            binding.priorityMedium.setImageResource(R.drawable.ic_done);
            binding.priorityLow.setImageResource(0);
            binding.priorityHigh.setImageResource(0);
        });
        binding.priorityHigh.setOnClickListener(v -> {
            priority = "3";
            binding.priorityHigh.setImageResource(R.drawable.ic_done);
            binding.priorityMedium.setImageResource(0);
            binding.priorityLow.setImageResource(0);
        });


        binding.confirm.setOnClickListener(view1 -> {
            title = binding.etTitle.getText().toString();
            subtitle = binding.etSubtitle.getText().toString();
            description = binding.etDescription.getText().toString();

            addNote(title, subtitle, description);
            Toast.makeText(view.getContext(), "Note updated successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });
    }
    private void addNote(String title, String subtitle, String description) {
        Note newNote = new Note();
        newNote.id = noteId;
        newNote.date = date;
        newNote.title = title;
        newNote.subtitle = subtitle;
        newNote.desc = description;
        newNote.priority = priority;
        viewModel.updateNote(newNote);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.note_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_delete) {
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            View view = LayoutInflater.from(getContext()).inflate(
                    R.layout.delete_bottom_sheet,
                    requireActivity().findViewById(R.id.bottomSheet));
            TextView yes, no;
            yes = view.findViewById(R.id.confirm_delete);
            no = view.findViewById(R.id.deny_delete);
            dialog.setContentView(view);
            dialog.show();
            yes.setOnClickListener(v -> {
                deleteNote();
                dialog.hide();
            });
            no.setOnClickListener(v -> dialog.hide());
        }
        return super.onOptionsItemSelected(item);
    }
    void deleteNote() {
        Note toDelete = new Note();
        toDelete.id = noteId;
        toDelete.title = title;
        toDelete.subtitle = subtitle;
        toDelete.date = date;
        toDelete.desc = description;
        toDelete.priority = priority;
        viewModel.deleteNote(toDelete);
        requireActivity().onBackPressed();
    }
}
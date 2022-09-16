package com.example.eventnotes.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventnotes.R;
import com.example.eventnotes.databinding.FragmentAddNoteBinding;
import com.example.eventnotes.entity.Note;
import com.example.eventnotes.viewModel.NoteViewModel;

import java.util.Date;

public class AddNoteFragment extends Fragment {
    private FragmentAddNoteBinding binding;
    private String title, subtitle, description;
    private NoteViewModel viewModel;
    private String priority = "1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        binding = FragmentAddNoteBinding.bind(view);
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
            Toast.makeText(view.getContext(), "Note added successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });

    }

    private void addNote(String title, String subtitle, String description) {
        Date curDate = new Date();
        CharSequence sequence = DateFormat.format("yyyy-MM-dd", curDate.getTime());

        Note newNote = new Note();
        newNote.date = sequence.toString();
        newNote.title = title;
        newNote.subtitle = subtitle;
        newNote.desc = description;
        newNote.priority = priority;
        newNote.userId = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE).getLong("userId", -1);
        Toast.makeText(requireActivity(), "Logged in: " + newNote.userId, Toast.LENGTH_SHORT).show();
        if (newNote.userId != -1) {
            viewModel.addNote(newNote, true);

        }
        else {
            Toast.makeText(requireActivity(), "ERROR!", Toast.LENGTH_SHORT).show();
        }
    }
}
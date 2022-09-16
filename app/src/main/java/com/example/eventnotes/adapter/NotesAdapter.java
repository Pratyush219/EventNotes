package com.example.eventnotes.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventnotes.R;
import com.example.eventnotes.entity.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    List<Note> notesDisplayed;
//    List<Note> allNotes;

    Context context;
    private static final String TAG = "NotesAdapter";
    public NoteCLickListener listener;
    public interface NoteCLickListener {
        void onNoteClick(Note note);
    }
    public void setNotesDisplayed(List<Note> notesDisplayed) {
        this.notesDisplayed = notesDisplayed;
//        allNotes = new ArrayList<>(notesDisplayed);
    }
    public void searchNotes(ArrayList<Note> filteredNotes) {
        this.notesDisplayed = filteredNotes;
        notifyDataSetChanged();
    }

    public NotesAdapter(Context context, NoteCLickListener noteCLickListener) {
        this.context = context;
        this.listener = noteCLickListener;
    }
    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notes, parent, false);
        return new NotesViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Note curNote = notesDisplayed.get(position);
        Log.d(TAG, "onBindViewHolder: " + curNote.title + " " + curNote.subtitle);
        holder.title.setText(curNote.id + " " + curNote.title);
        holder.subtitle.setText(curNote.subtitle);
        holder.date.setText(curNote.date);
        int res = 0;
        switch(curNote.priority) {
            case "1" :
                res = R.drawable.green_shape;
                break;
            case "2" :
                res = R.drawable.yellow_shape;
                break;
            case "3" :
                res = R.drawable.red_shape;
                break;
        }
        holder.priorityView.setBackgroundResource(res);
    }

    @Override
    public int getItemCount() {
        return notesDisplayed == null ? 0 : notesDisplayed.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, subtitle, date;
        public View priorityView;
        private final NoteCLickListener noteListener;
        public NotesViewHolder(@NonNull View itemView, NoteCLickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            subtitle = itemView.findViewById(R.id.tv_subtitle);
            date = itemView.findViewById(R.id.tv_date);
            priorityView = itemView.findViewById(R.id.view_priority);
            noteListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            noteListener.onNoteClick(notesDisplayed.get(getAdapterPosition()));
        }
    }
}

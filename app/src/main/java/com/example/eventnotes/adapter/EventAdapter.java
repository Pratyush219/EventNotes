package com.example.eventnotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventnotes.R;
import com.google.api.services.calendar.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private List<Event> events;
    private Context context;
    public EventClickListener listener;

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public interface EventClickListener {
        void onEventClick(Event event);
    }
    public EventAdapter(Context context) {
        this.context = context;
        events = new ArrayList<>();
    }
    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        Event event = events.get(position);
        Date eventStart = new Date(event.getStart().getDateTime().getValue());
        Date eventEnd = new Date(event.getEnd().getDateTime().getValue());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale.Builder().setRegion("IN").build());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", new Locale.Builder().setRegion("IN").build());

        holder.summary.setText(event.getSummary());
        holder.description.setText(event.getDescription());
        holder.start.setText(dateFormat.format(eventStart));
        holder.end.setText(timeFormat.format(eventEnd));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
    public static class EventHolder extends RecyclerView.ViewHolder {
        public TextView summary, description, date, start, end;
        public EventHolder(@NonNull View itemView) {
            super(itemView);
            summary = itemView.findViewById(R.id.tv_summary);
            description = itemView.findViewById(R.id.tv_description);
            date = itemView.findViewById(R.id.tv_date);
            start = itemView.findViewById(R.id.tv_start);
            end = itemView.findViewById(R.id.tv_end);
        }
    }
}

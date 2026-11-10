package org.nyimbo.agendi.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import org.nyimbo.agendi.R;
import org.nyimbo.agendi.model.EventEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.Holder> {
    public interface Actions { void details(EventEntity event); void bookmark(EventEntity event); void calendar(EventEntity event); void map(EventEntity event); }
    private final List<EventEntity> all = new ArrayList<>(), shown = new ArrayList<>();
    private final Actions actions;
    private String search = "", category = "All";
    private String dateFrom = "", dateTo = "";
    public EventAdapter(Actions actions) { this.actions = actions; }
    public void submit(List<EventEntity> events) { all.clear(); if (events != null) all.addAll(events); filter(); }
    public void setFilters(String search, String category) { this.search = search == null ? "" : search.toLowerCase(Locale.ROOT).trim(); this.category = category == null ? "All" : category; filter(); }
    public void setDateRange(String from, String to) { dateFrom = from == null ? "" : from; dateTo = to == null ? "" : to; filter(); }
    private void filter() {
        shown.clear();
        for (EventEntity e : all) {
            boolean categoryMatch = category.equals("All") || category.equalsIgnoreCase(e.category);
            boolean dateMatch = (dateFrom.isEmpty() || e.eventDate.compareTo(dateFrom) >= 0) && (dateTo.isEmpty() || e.eventDate.compareTo(dateTo) <= 0);
            String text = (e.title + " " + e.description + " " + e.venue + " " + e.eventDate + " " + e.category).toLowerCase(Locale.ROOT);
            if (categoryMatch && dateMatch && (search.isEmpty() || text.contains(search))) shown.add(e);
        }
        notifyDataSetChanged();
    }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) { return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event_row, parent, false)); }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        EventEntity event = shown.get(position);
        h.title.setText((event.demo ? "DEMO  ·  " : "") + event.title);
        String time = join(event.startTime, event.endTime, "–");
        String period = "";
        try { java.time.LocalDate date = java.time.LocalDate.parse(event.eventDate); java.time.LocalDate today = java.time.LocalDate.now();
            if (date.isBefore(today)) period = "PAST  ·  ";
            else if (date.isAfter(today)) period = "UPCOMING  ·  ";
            else if (event.startTime != null && event.endTime != null && java.time.LocalTime.now().isAfter(java.time.LocalTime.parse(event.startTime)) && java.time.LocalTime.now().isBefore(java.time.LocalTime.parse(event.endTime))) period = "ONGOING  ·  ";
            else period = "TODAY  ·  "; }
        catch (Exception ignored) { }
        h.details.setText(period + join(event.category, join(event.eventDate, time, "  ·  "), "  ·  ") + "\n" + (event.venue == null ? "Venue to be announced" : event.venue));
        if (event.imageUrl != null && !event.imageUrl.trim().isEmpty()) {
            h.image.setVisibility(View.VISIBLE); Glide.with(h.image).load(event.imageUrl).centerCrop().into(h.image);
        } else { h.image.setVisibility(View.GONE); Glide.with(h.image).clear(h.image); }
        h.bookmark.setText(event.bookmarked ? "Saved ✓" : "Save");
        h.bookmark.setOnClickListener(v -> actions.bookmark(event));
        h.calendar.setOnClickListener(v -> actions.calendar(event));
        h.map.setOnClickListener(v -> actions.map(event));
        h.itemView.setOnClickListener(v -> actions.details(event));
    }
    private static String join(String a, String b, String sep) { if (a == null || a.isEmpty()) return b == null ? "" : b; if (b == null || b.isEmpty()) return a; return a + sep + b; }
    @Override public int getItemCount() { return shown.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final TextView title, details; final ImageView image; final MaterialButton bookmark, calendar, map;
        Holder(View v) { super(v); title = v.findViewById(R.id.event_title); details = v.findViewById(R.id.event_details); image = v.findViewById(R.id.event_image); bookmark = v.findViewById(R.id.event_bookmark); calendar = v.findViewById(R.id.event_calendar); map = v.findViewById(R.id.event_map); }
    }
}

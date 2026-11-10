package org.nyimbo.agendi.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.datepicker.MaterialDatePicker;
import org.nyimbo.agendi.R;
import org.nyimbo.agendi.data.NyimboDatabase;
import org.nyimbo.agendi.model.EventEntity;
import org.nyimbo.agendi.sync.EventReminderScheduler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventsFragment extends Fragment {
    private EventAdapter adapter;
    private String search = "", category = "All";
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        RecyclerView list = root.findViewById(R.id.event_list); list.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EventAdapter(new EventAdapter.Actions() {
            @Override public void details(EventEntity e) { showDetails(e); }
            @Override public void bookmark(EventEntity e) {
                e.bookmarked = !e.bookmarked;
                NyimboDatabase.get(requireContext()).events().setBookmarked(e.id, e.bookmarked);
                if (e.bookmarked) EventReminderScheduler.schedule(requireContext(), e);
                else EventReminderScheduler.cancel(requireContext(), e.id);
                adapter.notifyDataSetChanged();
            }
            @Override public void calendar(EventEntity e) { addToCalendar(e); }
            @Override public void map(EventEntity e) { directions(e); }
        }); list.setAdapter(adapter);
        NyimboDatabase.get(requireContext()).events().all().observe(getViewLifecycleOwner(), adapter::submit);
        TextView newsPreview = root.findViewById(R.id.news_preview);
        NyimboDatabase.get(requireContext()).news().all().observe(getViewLifecycleOwner(), rows -> {
            if (rows == null || rows.isEmpty()) { newsPreview.setText("No announcements have been synchronized yet."); newsPreview.setOnClickListener(null); return; }
            StringBuilder summary = new StringBuilder(); for (int i = 0; i < Math.min(2, rows.size()); i++) {
                org.nyimbo.agendi.model.NewsEntity item = rows.get(i);
                if (summary.length() > 0) summary.append("\n\n");
                summary.append(item.demo ? "DEMO · " : "").append(item.title).append("\n").append(item.summary);
            }
            newsPreview.setText(summary.toString());
            newsPreview.setOnClickListener(v -> {
                StringBuilder all = new StringBuilder(); for (org.nyimbo.agendi.model.NewsEntity item : rows) {
                    if (all.length() > 0) all.append("\n\n────────\n\n");
                    all.append(item.demo ? "DEMO · " : "").append(item.title).append("\n\n").append(item.body);
                }
                new AlertDialog.Builder(requireContext()).setTitle("Church announcements").setMessage(all.toString()).setPositiveButton("Close", null).show();
            });
        });
        TextInputEditText searchBox = root.findViewById(R.id.event_search);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { search = s.toString(); adapter.setFilters(search, category); }
            @Override public void afterTextChanged(Editable s) {}
        });
        ChipGroup chips = root.findViewById(R.id.event_categories);
        chips.setOnCheckedStateChangeListener((group, ids) -> {
            category = "All";
            if (!ids.isEmpty()) { Chip selected = root.findViewById(ids.get(0)); if (selected != null) category = selected.getText().toString(); }
            adapter.setFilters(search, category);
        });
        Button dateFilter = root.findViewById(R.id.event_date_filter);
        dateFilter.setOnClickListener(v -> {
            MaterialDatePicker<androidx.core.util.Pair<Long,Long>> picker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Filter event dates").build();
            picker.addOnPositiveButtonClickListener(range -> {
                if (range == null || range.first == null || range.second == null) return;
                java.time.ZoneId zone = java.time.ZoneId.systemDefault();
                String from = java.time.Instant.ofEpochMilli(range.first).atZone(zone).toLocalDate().toString();
                String to = java.time.Instant.ofEpochMilli(range.second).atZone(zone).toLocalDate().toString();
                adapter.setDateRange(from, to); dateFilter.setText(from + " – " + to);
            });
            picker.show(getParentFragmentManager(), "event-date-range");
        });
        dateFilter.setOnLongClickListener(v -> { adapter.setDateRange("", ""); dateFilter.setText("Any date · Set range"); return true; });
        root.findViewById(R.id.open_all_map).setOnClickListener(v -> startActivity(new Intent(requireContext(), EventMapActivity.class)));
        return root;
    }
    private void showDetails(EventEntity e) {
        StringBuilder text = new StringBuilder();
        text.append(e.description == null ? "" : e.description).append("\n\n")
            .append("Category: ").append(or(e.category, "Uncategorized")).append("\n")
            .append("Date: ").append(or(e.eventDate, "To be announced")).append("\n")
            .append("Time: ").append(or(e.startTime, "")).append(e.endTime == null ? "" : " – " + e.endTime).append("\n")
            .append("Venue: ").append(or(e.venue, "To be announced")).append("\n")
            .append("Organizer: ").append(or(e.organizer, "Not provided"));
        if (e.contact != null && !e.contact.isEmpty()) text.append("\nContact: ").append(e.contact);
        new AlertDialog.Builder(requireContext()).setTitle((e.demo ? "DEMO · " : "") + e.title).setMessage(text.toString()).setPositiveButton("Close", null)
            .setNeutralButton("Directions", (d, w) -> directions(e)).show();
    }
    private void directions(EventEntity e) {
        Uri uri;
        if (e.latitude != null && e.longitude != null) uri = Uri.parse("google.navigation:q=" + e.latitude + "," + e.longitude);
        else if (e.venue != null && !e.venue.trim().isEmpty()) uri = Uri.parse("geo:0,0?q=" + Uri.encode(e.venue));
        else { Toast.makeText(requireContext(), "This event has no venue or coordinates yet.", Toast.LENGTH_LONG).show(); return; }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (e.latitude != null && e.longitude != null) intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(requireContext().getPackageManager()) == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(e.venue == null ? e.title : e.venue)));
        }
        try { startActivity(intent); } catch (Exception ex) { Toast.makeText(requireContext(), "No map app is available on this device.", Toast.LENGTH_LONG).show(); }
    }
    private void addToCalendar(EventEntity e) {
        long start = parseDate(e.eventDate, e.startTime), end = parseDate(e.eventDate, e.endTime);
        if (start == 0) { Toast.makeText(requireContext(), "This event has no valid date yet.", Toast.LENGTH_LONG).show(); return; }
        if (end == 0) end = start + 2 * 60 * 60 * 1000L;
        Intent insert = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, (e.demo ? "DEMO · " : "") + e.title)
            .putExtra(CalendarContract.Events.DESCRIPTION, e.description).putExtra(CalendarContract.Events.EVENT_LOCATION, e.venue)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start).putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        try { startActivity(Intent.createChooser(insert, "Add event to calendar")); }
        catch (Exception ex) { Toast.makeText(requireContext(), "Calendar app is unavailable.", Toast.LENGTH_LONG).show(); }
    }
    private long parseDate(String date, String time) {
        if (date == null) return 0;
        String stamp = date + " " + (time == null || time.isEmpty() ? "09:00" : time);
        try { Date parsed = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(stamp); return parsed == null ? 0 : parsed.getTime(); }
        catch (ParseException e) { return 0; }
    }
    private static String or(String s, String fallback) { return s == null || s.isEmpty() ? fallback : s; }
}

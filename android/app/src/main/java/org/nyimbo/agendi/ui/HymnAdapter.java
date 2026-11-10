package org.nyimbo.agendi.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.nyimbo.agendi.R;
import org.nyimbo.agendi.model.HymnEntity;
import java.util.ArrayList;
import java.util.List;

public class HymnAdapter extends RecyclerView.Adapter<HymnAdapter.Holder> {
    public interface Listener { void onHymn(HymnEntity hymn); }
    private final Listener listener;
    private final List<HymnEntity> rows = new ArrayList<>();
    public HymnAdapter(Listener listener) { this.listener = listener; }
    public void submit(List<HymnEntity> hymns) { rows.clear(); if (hymns != null) rows.addAll(hymns); notifyDataSetChanged(); }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_hymn_row, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        HymnEntity row = rows.get(position);
        holder.number.setText(String.format(java.util.Locale.getDefault(), "%d.", row.displayNumber()));
        holder.title.setText(row.title);
        holder.itemView.setContentDescription(row.displayNumber() + ". " + row.title);
        holder.itemView.setOnClickListener(v -> listener.onHymn(row));
    }
    @Override public int getItemCount() { return rows.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final TextView number, title;
        Holder(View view) { super(view); number = view.findViewById(R.id.hymn_number); title = view.findViewById(R.id.number_title); }
    }
}

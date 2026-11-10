package org.nyimbo.agendi.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.nyimbo.agendi.R;
import org.nyimbo.agendi.model.HymnEntity;
import java.util.List;

public class HymnsFragment extends Fragment {
    private static final String ARG_FAVORITES = "favorites";
    private boolean favorites;
    private boolean searchVisible;
    private String query = "";
    private RecyclerView list;
    private TextInputLayout searchContainer;
    private TextInputEditText searchField;
    private HymnViewModel viewModel;
    private HymnAdapter adapter;
    public static HymnsFragment newInstance(boolean favorites) {
        HymnsFragment fragment = new HymnsFragment(); Bundle args = new Bundle(); args.putBoolean(ARG_FAVORITES, favorites); fragment.setArguments(args); return fragment;
    }
    @Override public void onCreate(@Nullable Bundle saved) {
        super.onCreate(saved);
        favorites = getArguments() != null && getArguments().getBoolean(ARG_FAVORITES);
        if (saved != null) {
            query = saved.getString("query", "");
            searchVisible = saved.getBoolean("search_visible", !query.isEmpty());
        }
    }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saved) {
        View root = inflater.inflate(R.layout.fragment_hymns, container, false);
        searchContainer = root.findViewById(R.id.search_container);
        searchField = root.findViewById(R.id.hymn_search);
        searchContainer.setVisibility(searchVisible ? View.VISIBLE : View.GONE);
        searchField.setText(query);
        list = root.findViewById(R.id.hymn_list); list.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HymnAdapter(h -> ((MainActivity)requireActivity()).openReader(h.id)); list.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(HymnViewModel.class);
        viewModel.setFilter(2, query, favorites);
        TextView empty = root.findViewById(R.id.hymn_empty);
        viewModel.results().observe(getViewLifecycleOwner(), rows -> {
            adapter.submit(rows);
            boolean hasRows = rows != null && !rows.isEmpty();
            empty.setVisibility(hasRows ? View.GONE : View.VISIBLE);
            list.setVisibility(hasRows ? View.VISIBLE : View.GONE);
        });
        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { query = s.toString(); viewModel.setFilter(2, query, favorites); }
            @Override public void afterTextChanged(Editable s) {}
        });
        return root;
    }

    public boolean toggleSearch() {
        if (searchContainer == null || searchField == null) return false;
        searchVisible = !searchVisible;
        searchContainer.setVisibility(searchVisible ? View.VISIBLE : View.GONE);
        if (searchVisible) {
            searchField.requestFocus();
            searchField.post(() -> {
                if (isAdded() && searchField != null) {
                    InputMethodManager keyboard = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        } else {
            InputMethodManager keyboard = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            searchField.setText("");
            searchField.clearFocus();
        }
        return searchVisible;
    }

    public boolean isSearchVisible() { return searchVisible; }

    @Override public void onSaveInstanceState(@NonNull Bundle out) {
        out.putString("query", query);
        out.putBoolean("search_visible", searchVisible);
        super.onSaveInstanceState(out);
    }
}

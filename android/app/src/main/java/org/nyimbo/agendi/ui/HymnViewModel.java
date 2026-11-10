package org.nyimbo.agendi.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import org.nyimbo.agendi.model.HymnEntity;
import org.nyimbo.agendi.repository.HymnRepository;
import java.util.List;

public class HymnViewModel extends AndroidViewModel {
    private static final class Filter { final int version; final String query; final boolean favorites;
        Filter(int version, String query, boolean favorites) { this.version = version; this.query = query; this.favorites = favorites; } }
    private final MutableLiveData<Filter> filter = new MutableLiveData<>(new Filter(0, "", false));
    private final HymnRepository repository;
    private final LiveData<List<HymnEntity>> results;
    public HymnViewModel(@NonNull Application application) {
        super(application); repository = HymnRepository.get(application);
        results = Transformations.switchMap(filter, f -> f.favorites ? repository.favorites() : repository.search(f.version, f.query));
    }
    public LiveData<List<HymnEntity>> results() { return results; }
    public void setFilter(int version, String query, boolean favorites) { filter.setValue(new Filter(version, query, favorites)); }
}

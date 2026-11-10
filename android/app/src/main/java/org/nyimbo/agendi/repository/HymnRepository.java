package org.nyimbo.agendi.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import org.nyimbo.agendi.data.NyimboDatabase;
import org.nyimbo.agendi.model.HymnEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HymnRepository {
    private static volatile HymnRepository instance;
    private final NyimboDatabase db;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private HymnRepository(Context context) { db = NyimboDatabase.get(context); }
    public static HymnRepository get(Context context) {
        if (instance == null) synchronized (HymnRepository.class) { if (instance == null) instance = new HymnRepository(context.getApplicationContext()); }
        return instance;
    }
    public LiveData<List<HymnEntity>> search(int version, String query) { return db.hymns().search(version, query == null ? "" : query.trim()); }
    public LiveData<List<HymnEntity>> favorites() { return db.hymns().favorites(); }
    public LiveData<List<HymnEntity>> recent(int limit) { return db.hymns().recent(limit); }
    public HymnEntity get(long id) { return db.hymns().get(id); }
    public void favorite(long id, boolean value) { io.execute(() -> db.hymns().setFavorite(id, value)); }
    public void notes(long id, String value) { io.execute(() -> db.hymns().saveNotes(id, value)); }
    public void viewed(long id) { io.execute(() -> db.hymns().markViewed(id, System.currentTimeMillis())); }
}

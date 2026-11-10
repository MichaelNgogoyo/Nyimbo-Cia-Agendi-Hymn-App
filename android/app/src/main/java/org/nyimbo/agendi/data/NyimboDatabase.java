package org.nyimbo.agendi.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nyimbo.agendi.BuildConfig;
import org.nyimbo.agendi.model.EventEntity;
import org.nyimbo.agendi.model.HymnEntity;
import org.nyimbo.agendi.model.NewsEntity;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {HymnEntity.class, EventEntity.class, NewsEntity.class}, version = 2, exportSchema = true)
public abstract class NyimboDatabase extends RoomDatabase {
    private static volatile NyimboDatabase instance;
    private static volatile boolean sourceCatalogSeedStarted;
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE hymns ADD COLUMN sourceUrl TEXT");
        }
    };
    public abstract HymnDao hymns();
    public abstract EventDao events();
    public abstract NewsDao news();

    public static NyimboDatabase get(Context context) {
        if (instance == null) synchronized (NyimboDatabase.class) {
            if (instance == null) instance = Room.databaseBuilder(context.getApplicationContext(), NyimboDatabase.class, "nyimbo.db")
                .addMigrations(MIGRATION_1_2)
                .addCallback(new Callback() {
                    @Override public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        if (BuildConfig.OFFLINE_RELEASE) return;
                        Executors.newSingleThreadExecutor().execute(() -> {
                            instance.hymns().insertAll(Arrays.asList(
                                new HymnEntity(1, 0, "Demo record 001 — content not installed", "Demo record only.\nVerified Kikuyu hymn content has not been installed.", null, null, null, true, false, "", 0, null),
                                new HymnEntity(1, 1, "Demo record 001 — content not installed", "Demo record only.\nVerified Kikuyu hymn content has not been installed.", null, null, null, true, false, "", 0, null)));
                            instance.events().upsertAll(Arrays.asList(
                                new EventEntity("demo-sabbath", "Sabbath worship", "Sample event used to preview event features. This is not a published church event.", "Sabbath Worship", "2026-12-05", "09:00", "12:00", "Demo venue — replace with verified venue", "Demo organizer", null, null, null, null, false, true),
                                new EventEntity("demo-youth", "Youth choir program", "Sample event used to preview search, bookmarks and calendar actions.", "Choir Events", "2026-12-12", "15:00", "17:00", "Demo venue — replace with verified venue", "Demo organizer", null, null, null, null, false, true)));
                        });
                    }
                }).build();
        }
        if (!sourceCatalogSeedStarted) synchronized (NyimboDatabase.class) {
            if (!sourceCatalogSeedStarted) {
                sourceCatalogSeedStarted = true;
                Context appContext = context.getApplicationContext();
                NyimboDatabase db = instance;
                Executors.newSingleThreadExecutor().execute(() -> seedSourceCatalog(appContext, db));
            }
        }
        return instance;
    }

    private static void seedSourceCatalog(Context context, NyimboDatabase db) {
        try {
            if (db.hymns().countByVersion(2) > 0) return;
            byte[] bytes;
            try (InputStream in = context.getAssets().open("mysongbooks_nyimbo_cia_agendi.json");
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192]; int read;
                while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
                bytes = out.toByteArray();
            }
            JSONObject root = new JSONObject(new String(bytes, StandardCharsets.UTF_8));
            JSONArray entries = root.getJSONArray("entries");
            List<HymnEntity> songs = new ArrayList<>(entries.length());
            for (int i = 0; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                songs.add(new HymnEntity(entry.getInt("source_order"), 2, entry.getString("title"),
                    entry.getString("lyrics"), null, null, null, false, false, "", 0,
                    entry.optString("source_url", null)));
            }
            db.hymns().insertAll(songs);
        } catch (Exception e) {
            Log.e("NyimboDatabase", "Could not load offline MySongbooks catalog", e);
        }
    }
}

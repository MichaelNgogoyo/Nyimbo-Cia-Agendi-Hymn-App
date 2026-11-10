package org.nyimbo.agendi.ui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.nyimbo.agendi.R;
import org.nyimbo.agendi.data.NyimboDatabase;
import org.nyimbo.agendi.model.HymnEntity;
import org.nyimbo.agendi.repository.HymnRepository;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReaderActivity extends AppCompatActivity {
    public static final String EXTRA_HYMN_ID = "hymn_id";
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private HymnEntity hymn;
    private TextView number, title, lyrics;
    private ImageButton favoriteAction;

    @Override protected void onCreate(@Nullable Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_reader);
        SystemBarInsets.apply(this, findViewById(R.id.reader_root));
        findViewById(R.id.reader_back).setOnClickListener(v -> finish());
        favoriteAction = findViewById(R.id.reader_favorite);
        favoriteAction.setOnClickListener(v -> toggleFavorite());
        number = findViewById(R.id.reader_number);
        title = findViewById(R.id.reader_title);
        lyrics = findViewById(R.id.reader_lyrics);
        load(getIntent().getLongExtra(EXTRA_HYMN_ID, -1));
    }

    private void load(long id) {
        io.execute(() -> {
            HymnEntity loaded = NyimboDatabase.get(this).hymns().get(id);
            if (loaded == null) {
                runOnUiThread(this::finish);
                return;
            }
            NyimboDatabase.get(this).hymns().markViewed(id, System.currentTimeMillis());
            runOnUiThread(() -> render(loaded));
        });
    }

    private void render(HymnEntity hymn) {
        this.hymn = hymn;
        number.setText(String.format(Locale.getDefault(), "%d.", hymn.displayNumber()));
        title.setText(hymn.title);
        if (hymn.lyrics == null || hymn.lyrics.trim().isEmpty()) {
            lyrics.setText(R.string.lyrics_unavailable);
        } else {
            String spacedLyrics = hymn.lyrics.replace("\r\n", "\n").replace('\r', '\n')
                .replaceAll("\\n(?=\\[\\d+\\]\\n)", "\n\n");
            lyrics.setText(spacedLyrics);
        }
        updateFavoriteAction();
    }

    private void toggleFavorite() {
        if (hymn == null) return;
        hymn.favorite = !hymn.favorite;
        updateFavoriteAction();
        HymnRepository.get(this).favorite(hymn.id, hymn.favorite);
    }

    private void updateFavoriteAction() {
        if (favoriteAction == null || hymn == null) return;
        favoriteAction.setImageResource(hymn.favorite
            ? R.drawable.ic_round_favorite_saved
            : R.drawable.ic_round_favorite);
        favoriteAction.setContentDescription(getString(hymn.favorite
            ? R.string.reader_unfavorite : R.string.reader_favorite));
    }

    @Override protected void onDestroy() {
        io.shutdown();
        super.onDestroy();
    }
}

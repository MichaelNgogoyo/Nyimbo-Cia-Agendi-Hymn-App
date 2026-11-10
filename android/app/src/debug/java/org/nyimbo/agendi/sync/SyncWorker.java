package org.nyimbo.agendi.sync;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.nyimbo.agendi.data.NyimboDatabase;
import org.nyimbo.agendi.model.EventEntity;
import org.nyimbo.agendi.model.HymnEntity;
import org.nyimbo.agendi.model.NewsEntity;
import org.nyimbo.agendi.network.ApiClient;
import org.nyimbo.agendi.network.EventDto;
import org.nyimbo.agendi.network.HymnDto;
import org.nyimbo.agendi.network.NewsDto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;

public class SyncWorker extends Worker {
    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) { super(context, params); }
    @NonNull @Override public Result doWork() {
        try {
            NyimboDatabase db = NyimboDatabase.get(getApplicationContext());
            Response<List<EventDto>> events = ApiClient.create().events().execute();
            if (events.isSuccessful() && events.body() != null) {
                List<EventEntity> rows = new ArrayList<>(); for (EventDto dto : events.body()) rows.add(dto.toEntity());
                if (!rows.isEmpty()) db.events().syncAll(rows);
            }
            Response<List<NewsDto>> news = ApiClient.create().news().execute();
            if (news.isSuccessful() && news.body() != null) {
                List<NewsEntity> rows = new ArrayList<>(); for (NewsDto dto : news.body()) rows.add(dto.toEntity());
                if (!rows.isEmpty()) db.news().upsertAll(rows);
            }
            for (int version = 0; version <= 1; version++) {
                Response<List<HymnDto>> hymns = ApiClient.create().hymns(version).execute();
                if (hymns.isSuccessful() && hymns.body() != null) {
                    List<HymnEntity> rows = new ArrayList<>(); for (HymnDto dto : hymns.body()) rows.add(dto.toEntity());
                    if (!rows.isEmpty()) db.hymns().syncAll(rows);
                }
            }
            return Result.success();
        } catch (Exception error) { return Result.retry(); }
    }
}

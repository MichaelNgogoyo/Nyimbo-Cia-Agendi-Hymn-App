package org.nyimbo.agendi.sync;

import android.content.Context;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import org.nyimbo.agendi.model.EventEntity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class EventReminderScheduler {
    private EventReminderScheduler() {}
    public static void schedule(Context context, EventEntity e) {
        if (!context.getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("eventNotifications", false)) return;
        try {
            LocalDateTime start = LocalDateTime.parse(e.eventDate + "T" + (e.startTime == null ? "09:00" : e.startTime), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            if (!start.isAfter(LocalDateTime.now())) return;
            long delay = Math.max(0, Duration.between(LocalDateTime.now(), start.minusDays(1)).toMillis());
            Data data = new Data.Builder().putString("eventId", e.id).putString("title", e.title)
                .putString("venue", e.venue == null ? "" : e.venue).putString("eventDate", e.eventDate).build();
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(EventReminderWorker.class).setInputData(data).setInitialDelay(delay, TimeUnit.MILLISECONDS).build();
            WorkManager.getInstance(context).enqueueUniqueWork("event-reminder-" + e.id, ExistingWorkPolicy.REPLACE, request);
        } catch (Exception ignored) { }
    }
    public static void cancel(Context context, String id) { WorkManager.getInstance(context).cancelUniqueWork("event-reminder-" + id); }
    public static void scheduleBookmarked(Context context) {
        Context app = context.getApplicationContext();
        Executors.newSingleThreadExecutor().execute(() -> {
            for (EventEntity event : org.nyimbo.agendi.data.NyimboDatabase.get(app).events().bookmarksBlocking()) schedule(app, event);
        });
    }
}

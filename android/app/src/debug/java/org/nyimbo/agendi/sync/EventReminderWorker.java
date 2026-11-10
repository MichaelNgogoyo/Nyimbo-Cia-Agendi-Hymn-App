package org.nyimbo.agendi.sync;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.nyimbo.agendi.R;

public class EventReminderWorker extends Worker {
    private static final String CHANNEL = "event_reminders";
    public EventReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) { super(context, params); }
    @NonNull @Override public Result doWork() {
        Context context = getApplicationContext();
        if (!context.getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("eventNotifications", false)) return Result.success();
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return Result.success();
        NotificationManager manager = context.getSystemService(NotificationManager.class); if (manager == null) return Result.success();
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(new NotificationChannel(CHANNEL, "Event reminders", NotificationManager.IMPORTANCE_DEFAULT));
        String title = getInputData().getString("title"); String venue = getInputData().getString("venue"); String date = getInputData().getString("eventDate");
        String eventId = getInputData().getString("eventId");
        manager.notify((eventId == null ? "event" : eventId).hashCode(), new NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_app).setContentTitle(title == null ? "Upcoming church event" : "Tomorrow: " + title)
            .setContentText((date == null ? "" : date) + (venue == null || venue.isEmpty() ? "" : " · " + venue)).setAutoCancel(true).build());
        return Result.success();
    }
}

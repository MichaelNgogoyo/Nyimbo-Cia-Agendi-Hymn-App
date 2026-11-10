package org.nyimbo.agendi.sync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.nyimbo.agendi.R;

public class NyimboMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL = "church_updates";
    @Override public void onMessageReceived(RemoteMessage message) {
        String title = message.getNotification() == null ? "Church update" : message.getNotification().getTitle();
        String body = message.getNotification() == null ? "There is a new update." : message.getNotification().getBody();
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager == null) return;
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(new NotificationChannel(CHANNEL, "Church updates", NotificationManager.IMPORTANCE_DEFAULT));
        manager.notify((int) System.currentTimeMillis(), new NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_app).setContentTitle(title).setContentText(body).setAutoCancel(true).build());
    }
}

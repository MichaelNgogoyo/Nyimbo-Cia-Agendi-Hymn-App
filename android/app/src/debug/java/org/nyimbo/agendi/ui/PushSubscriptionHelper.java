package org.nyimbo.agendi.ui;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public final class PushSubscriptionHelper {
    private PushSubscriptionHelper() {}
    public static void update(Context context, boolean enabled) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            if (enabled) Toast.makeText(context, "On-device reminders are enabled. Remote updates need Firebase setup.", Toast.LENGTH_LONG).show();
            return;
        }
        if (enabled) FirebaseMessaging.getInstance().subscribeToTopic("church_updates");
        else FirebaseMessaging.getInstance().unsubscribeFromTopic("church_updates");
    }
}

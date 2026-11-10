package org.nyimbo.agendi.config;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PushNotificationService {
    private final FirebaseMessaging messaging;
    public PushNotificationService(org.springframework.beans.factory.ObjectProvider<FirebaseMessaging> messaging) { this.messaging = messaging.getIfAvailable(); }
    public String send(NotificationRequest request) {
        if (messaging == null) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "FCM is not configured. Set FCM_ENABLED and application-default Firebase credentials.");
        Map<String,String> data = new HashMap<>(); data.put("kind", "church_update");
        if (request.eventId() != null && !request.eventId().isBlank()) data.put("eventId", request.eventId());
        try {
            Message message = Message.builder().setTopic("church_updates")
                .setNotification(com.google.firebase.messaging.Notification.builder().setTitle(request.title()).setBody(request.body()).build())
                .putAllData(data).build();
            return messaging.send(message);
        } catch (Exception error) { throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Firebase Cloud Messaging could not send the notification."); }
    }
}

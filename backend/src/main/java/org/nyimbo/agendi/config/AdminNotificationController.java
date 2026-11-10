package org.nyimbo.agendi.config;

import jakarta.validation.Valid;
import org.nyimbo.agendi.audit.AuditService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController @RequestMapping("/api/v1/admin/notifications")
public class AdminNotificationController {
    private final PushNotificationService notifications; private final AuditService audit;
    public AdminNotificationController(PushNotificationService notifications, AuditService audit) { this.notifications = notifications; this.audit = audit; }
    @PostMapping public Map<String,String> send(@Valid @RequestBody NotificationRequest request) {
        String messageId = notifications.send(request); audit.record("SEND", "notification", request.eventId(), request.title()); return Map.of("messageId", messageId);
    }
}

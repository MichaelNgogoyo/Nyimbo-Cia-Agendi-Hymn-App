package org.nyimbo.agendi.audit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditRepository repository;
    public AuditService(AuditRepository repository) { this.repository = repository; }
    public void record(String action, String type, Object id, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuditEntity row = new AuditEntity(); row.actor = auth == null ? "system" : auth.getName();
        row.action = action; row.entityType = type; row.entityId = id == null ? null : id.toString(); row.details = details;
        repository.save(row);
    }
}

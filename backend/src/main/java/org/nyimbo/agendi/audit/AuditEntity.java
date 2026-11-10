package org.nyimbo.agendi.audit;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "admin_audit")
public class AuditEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false, length = 160) public String actor;
    @Column(nullable = false, length = 60) public String action;
    @Column(name = "entity_type", nullable = false, length = 60) public String entityType;
    @Column(name = "entity_id", length = 100) public String entityId;
    @Column(columnDefinition = "text") public String details;
    @Column(name = "occurred_at", nullable = false) public LocalDateTime occurredAt;
    @PrePersist void created() { if (occurredAt == null) occurredAt = LocalDateTime.now(); }
}

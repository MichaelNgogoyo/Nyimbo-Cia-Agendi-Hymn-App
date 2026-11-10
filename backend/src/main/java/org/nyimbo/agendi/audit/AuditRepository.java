package org.nyimbo.agendi.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditRepository extends JpaRepository<AuditEntity, Long> {
    List<AuditEntity> findTop200ByOrderByOccurredAtDesc();
}

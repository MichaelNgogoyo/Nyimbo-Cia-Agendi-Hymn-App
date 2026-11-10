package org.nyimbo.agendi.audit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController @RequestMapping("/api/v1/admin/audit")
public class AdminAuditController {
    private final AuditRepository repository;
    public AdminAuditController(AuditRepository repository) { this.repository = repository; }
    @GetMapping public List<AuditEntity> recent() { return repository.findTop200ByOrderByOccurredAtDesc(); }
}

package org.nyimbo.agendi.content;

import org.nyimbo.agendi.audit.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/admin/hymns")
public class AdminHymnController {
    private final HymnRepository repository; private final AuditService audit;
    public AdminHymnController(HymnRepository repository, AuditService audit) { this.repository = repository; this.audit = audit; }
    @GetMapping public List<HymnEntity> all() { return repository.findAll().stream().sorted((a,b) -> { int v = Integer.compare(a.version,b.version); return v == 0 ? Integer.compare(a.number,b.number) : v; }).toList(); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void remove(@PathVariable long id) {
        HymnEntity row = repository.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
        repository.delete(row); audit.record("DELETE", "hymn", id, "Removed verified hymn #" + row.number + " from version " + row.version);
    }
}

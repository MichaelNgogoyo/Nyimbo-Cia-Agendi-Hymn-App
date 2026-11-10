package org.nyimbo.agendi.content;

import org.nyimbo.agendi.audit.AuditService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/hymns")
public class HymnController {
    private final HymnRepository repository; private final HymnImportService importer; private final AuditService audit;
    public HymnController(HymnRepository repository, HymnImportService importer, AuditService audit) { this.repository = repository; this.importer = importer; this.audit = audit; }
    @GetMapping public List<HymnEntity> publicHymns(@RequestParam int version) {
        if (version != 0 && version != 1) throw new IllegalArgumentException("Version must be 0 (old) or 1 (new).");
        return repository.findByVersionAndVerifiedTrueOrderByNumberAsc(version);
    }
    @PostMapping("/import") public List<HymnEntity> importHymns(@RequestBody List<HymnImportRecord> records) {
        List<HymnEntity> saved = importer.importVerified(records); audit.record("IMPORT", "hymns", saved.size(), "Imported authorized verified hymn records."); return saved;
    }
}

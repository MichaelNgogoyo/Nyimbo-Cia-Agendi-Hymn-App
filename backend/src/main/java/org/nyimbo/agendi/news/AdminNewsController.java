package org.nyimbo.agendi.news;

import org.nyimbo.agendi.audit.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.List;

@RestController @RequestMapping("/api/v1/admin/news")
public class AdminNewsController {
    private final NewsRepository repository; private final AuditService audit;
    public AdminNewsController(NewsRepository repository, AuditService audit) { this.repository = repository; this.audit = audit; }
    @GetMapping public List<NewsEntity> all() { return repository.findAllByOrderByCreatedAtDesc(); }
    @PostMapping public NewsEntity create(@Valid @RequestBody NewsRequest request) {
        NewsEntity row = new NewsEntity(); request.apply(row); NewsEntity saved = repository.save(row); audit.record("CREATE", "announcement", saved.id, saved.title); return saved;
    }
    @PutMapping("/{id}") public NewsEntity update(@PathVariable long id, @Valid @RequestBody NewsRequest request) {
        NewsEntity row = require(id); request.apply(row); row.archived = false; NewsEntity saved = repository.save(row); audit.record("UPDATE", "announcement", id, saved.title); return saved;
    }
    @PostMapping("/{id}/publish") public NewsEntity publish(@PathVariable long id, @RequestParam boolean published) {
        NewsEntity row = require(id); row.published = published; row.archived = false; row.demo = false;
        NewsEntity saved = repository.save(row); audit.record(published ? "PUBLISH" : "UNPUBLISH", "announcement", id, saved.title); return saved;
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archive(@PathVariable long id) {
        NewsEntity row = require(id); row.archived = true; row.published = false; repository.save(row); audit.record("ARCHIVE", "announcement", id, row.title);
    }
    private NewsEntity require(long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); }
}

package org.nyimbo.agendi.events;

import org.nyimbo.agendi.audit.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.List;

@RestController @RequestMapping("/api/v1/admin/events")
public class AdminEventController {
    private final EventRepository repository; private final AuditService audit;
    public AdminEventController(EventRepository repository, AuditService audit) { this.repository = repository; this.audit = audit; }
    @GetMapping public List<EventEntity> all() { return repository.findAllByOrderByEventDateAscStartTimeAsc(); }
    @PostMapping public EventEntity create(@Valid @RequestBody EventRequest request) {
        EventEntity row = new EventEntity(); request.apply(row); EventEntity saved = repository.save(row);
        audit.record("CREATE", "event", saved.id, saved.title); return saved;
    }
    @PutMapping("/{id}") public EventEntity update(@PathVariable long id, @Valid @RequestBody EventRequest request) {
        EventEntity row = require(id); request.apply(row); row.archived = false; EventEntity saved = repository.save(row);
        audit.record("UPDATE", "event", id, saved.title); return saved;
    }
    @PostMapping("/{id}/publish") public EventEntity publish(@PathVariable long id, @RequestParam boolean published) {
        EventEntity row = require(id); row.published = published; row.archived = false; row.demo = false;
        EventEntity saved = repository.save(row); audit.record(published ? "PUBLISH" : "UNPUBLISH", "event", id, saved.title); return saved;
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archive(@PathVariable long id) {
        EventEntity row = require(id); row.archived = true; row.published = false; repository.save(row); audit.record("ARCHIVE", "event", id, row.title);
    }
    private EventEntity require(long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); }
}

package org.nyimbo.agendi.events;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RestController @RequestMapping("/api/v1/events")
public class EventController {
    private final EventRepository repository;
    public EventController(EventRepository repository) { this.repository = repository; }
    @GetMapping public List<EventEntity> publicEvents(@RequestParam(required=false) String category,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required=false) String q) {
        String query = q == null ? "" : q.toLowerCase(Locale.ROOT).trim();
        return repository.findByPublishedTrueAndArchivedFalseOrderByEventDateAscStartTimeAsc().stream()
            .filter(e -> category == null || category.equalsIgnoreCase(e.category))
            .filter(e -> from == null || !e.eventDate.isBefore(from))
            .filter(e -> to == null || !e.eventDate.isAfter(to))
            .filter(e -> query.isEmpty() || (e.title + " " + e.description + " " + e.venue + " " + e.category).toLowerCase(Locale.ROOT).contains(query)).toList();
    }
}

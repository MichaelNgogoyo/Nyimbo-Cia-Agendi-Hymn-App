package org.nyimbo.agendi.news;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/news")
public class NewsController {
    private final NewsRepository repository;
    public NewsController(NewsRepository repository) { this.repository = repository; }
    @GetMapping public List<NewsEntity> publicNews() { return repository.findByPublishedTrueAndArchivedFalseOrderByPublishedAtDesc(); }
}

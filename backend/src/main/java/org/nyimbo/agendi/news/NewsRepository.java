package org.nyimbo.agendi.news;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    List<NewsEntity> findByPublishedTrueAndArchivedFalseOrderByPublishedAtDesc();
    List<NewsEntity> findAllByOrderByCreatedAtDesc();
}

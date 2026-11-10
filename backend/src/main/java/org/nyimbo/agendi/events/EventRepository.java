package org.nyimbo.agendi.events;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findByPublishedTrueAndArchivedFalseOrderByEventDateAscStartTimeAsc();
    List<EventEntity> findAllByOrderByEventDateAscStartTimeAsc();
}

package org.nyimbo.agendi.events;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity @Table(name = "events")
public class EventEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false, length = 140) public String title;
    @Column(nullable = false, columnDefinition = "text") public String description;
    @Column(nullable = false, length = 40) public String category;
    @Column(name = "event_date", nullable = false) public LocalDate eventDate;
    @Column(name = "start_time", nullable = false) public LocalTime startTime;
    @Column(name = "end_time", nullable = false) public LocalTime endTime;
    @Column(nullable = false, length = 240) public String venue;
    @Column(nullable = false, length = 140) public String organizer;
    @Column(name = "image_url", length = 500) public String imageUrl;
    @Column(length = 200) public String contact;
    public Double latitude;
    public Double longitude;
    @Column(nullable = false) public boolean published;
    @Column(nullable = false) public boolean archived;
    @Column(nullable = false) public boolean demo;
    @Column(name = "created_at", nullable = false) public LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) public LocalDateTime updatedAt;
    @PrePersist void created() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void changed() { updatedAt = LocalDateTime.now(); }
}

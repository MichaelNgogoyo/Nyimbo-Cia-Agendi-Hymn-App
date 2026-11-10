package org.nyimbo.agendi.news;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "announcements")
public class NewsEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false, length = 160) public String title;
    @Column(nullable = false, length = 400) public String summary;
    @Column(nullable = false, columnDefinition = "text") public String body;
    @Column(name = "image_url", length = 500) public String imageUrl;
    @Column(nullable = false) public boolean published;
    @Column(nullable = false) public boolean archived;
    @Column(nullable = false) public boolean demo;
    @Column(name = "published_at") public LocalDateTime publishedAt;
    @Column(name = "created_at", nullable = false) public LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) public LocalDateTime updatedAt;
    @PrePersist void created() { createdAt = updatedAt = LocalDateTime.now(); if (published && publishedAt == null) publishedAt = createdAt; }
    @PreUpdate void changed() { updatedAt = LocalDateTime.now(); if (published && publishedAt == null) publishedAt = updatedAt; }
}

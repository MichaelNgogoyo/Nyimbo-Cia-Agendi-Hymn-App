package org.nyimbo.agendi.content;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "hymns", uniqueConstraints = @UniqueConstraint(name = "uq_hymns_version_number", columnNames = {"version", "number"}))
public class HymnEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false) public int version;
    @Column(nullable = false) public int number;
    @Column(nullable = false, length = 240) public String title;
    @Column(nullable = false, columnDefinition = "text") public String lyrics;
    @Column(name = "music_key", length = 80) public String musicKey;
    @Column(name = "cross_reference_number") public Integer crossReferenceNumber;
    @Column(name = "notation_url", length = 500) public String notationUrl;
    @Column(name = "content_license", nullable = false, length = 200) public String contentLicense;
    @Column(name = "license_reference", nullable = false, length = 500) public String licenseReference;
    @Column(nullable = false) public boolean verified;
    @Column(nullable = false) public boolean demo;
    @Column(name = "updated_at", nullable = false) public LocalDateTime updatedAt;
    @PrePersist @PreUpdate void changed() { updatedAt = LocalDateTime.now(); }
}

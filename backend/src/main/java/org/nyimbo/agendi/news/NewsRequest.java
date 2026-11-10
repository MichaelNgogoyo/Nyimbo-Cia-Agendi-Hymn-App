package org.nyimbo.agendi.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewsRequest(@NotBlank @Size(max=160) String title, @NotBlank @Size(max=400) String summary,
                          @NotBlank @Size(max=10000) String body, @Size(max=500) String imageUrl,
                          boolean published, boolean demo) {
    public void apply(NewsEntity e) {
        if (imageUrl != null && !imageUrl.isBlank() && !(imageUrl.startsWith("https://") || imageUrl.startsWith("/uploads/"))) throw new IllegalArgumentException("Announcement images must use HTTPS or an uploaded /uploads/ path.");
        e.title = title; e.summary = summary; e.body = body; e.imageUrl = imageUrl; e.published = published; e.demo = demo;
    }
}

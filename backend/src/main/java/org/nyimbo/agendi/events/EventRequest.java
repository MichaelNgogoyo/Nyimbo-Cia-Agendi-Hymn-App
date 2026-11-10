package org.nyimbo.agendi.events;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventRequest(
    @NotBlank @Size(max=140) String title,
    @NotBlank @Size(max=8000) String description,
    @NotBlank @Pattern(regexp="Camp Meetings|Sabbath Worship|Crusades|Youth Events|Choir Events|Conferences|Special Programs") String category,
    @NotNull LocalDate eventDate,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    @NotBlank @Size(max=240) String venue,
    @NotBlank @Size(max=140) String organizer,
    @Size(max=500) String imageUrl,
    @Size(max=200) String contact,
    @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
    boolean published,
    boolean demo
) {
    public void apply(EventEntity e) {
        if ((latitude == null) != (longitude == null)) throw new IllegalArgumentException("Latitude and longitude must be supplied together.");
        if (endTime.isBefore(startTime)) throw new IllegalArgumentException("Event end time must be after its start time.");
        if (imageUrl != null && !imageUrl.isBlank() && !(imageUrl.startsWith("https://") || imageUrl.startsWith("/uploads/"))) throw new IllegalArgumentException("Event images must use HTTPS or an uploaded /uploads/ path.");
        e.title = title; e.description = description; e.category = category; e.eventDate = eventDate;
        e.startTime = startTime; e.endTime = endTime; e.venue = venue; e.organizer = organizer;
        e.imageUrl = imageUrl; e.contact = contact; e.latitude = latitude; e.longitude = longitude;
        e.published = published; e.demo = demo;
    }
}

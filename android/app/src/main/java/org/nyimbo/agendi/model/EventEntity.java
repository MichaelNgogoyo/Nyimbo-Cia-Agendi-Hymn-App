package org.nyimbo.agendi.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "events")
public class EventEntity {
    @NonNull @PrimaryKey public String id;
    public String title;
    public String description;
    public String category;
    public String eventDate;
    public String startTime;
    public String endTime;
    public String venue;
    public String organizer;
    public String imageUrl;
    public String contact;
    public Double latitude;
    public Double longitude;
    public boolean bookmarked;
    public boolean demo;

    public EventEntity(String id, String title, String description, String category, String eventDate,
                       String startTime, String endTime, String venue, String organizer, String imageUrl,
                       String contact, Double latitude, Double longitude, boolean bookmarked, boolean demo) {
        this.id = id; this.title = title; this.description = description; this.category = category;
        this.eventDate = eventDate; this.startTime = startTime; this.endTime = endTime; this.venue = venue;
        this.organizer = organizer; this.imageUrl = imageUrl; this.contact = contact;
        this.latitude = latitude; this.longitude = longitude; this.bookmarked = bookmarked; this.demo = demo;
    }
}

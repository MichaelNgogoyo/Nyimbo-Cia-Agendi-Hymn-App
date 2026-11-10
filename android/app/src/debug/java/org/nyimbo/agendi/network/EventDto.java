package org.nyimbo.agendi.network;

import org.nyimbo.agendi.model.EventEntity;

public class EventDto {
    public String id, title, description, category, eventDate, startTime, endTime, venue, organizer, imageUrl, contact;
    public Double latitude, longitude;
    public EventEntity toEntity() { return new EventEntity(id, title, description, category, eventDate, startTime,
        endTime, venue, organizer, imageUrl, contact, latitude, longitude, false, false); }
}

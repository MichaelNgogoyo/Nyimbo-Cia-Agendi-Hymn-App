package org.nyimbo.agendi.network;

import org.nyimbo.agendi.model.NewsEntity;

public class NewsDto {
    public long id;
    public String title, summary, body, imageUrl, publishedAt;
    public boolean demo;
    public NewsEntity toEntity() { return new NewsEntity(String.valueOf(id), title, summary, body, imageUrl, publishedAt, demo); }
}

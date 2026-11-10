package org.nyimbo.agendi.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "news")
public class NewsEntity {
    @NonNull @PrimaryKey public String id;
    public String title;
    public String summary;
    public String body;
    public String imageUrl;
    public String publishedAt;
    public boolean demo;
    public NewsEntity(String id, String title, String summary, String body, String imageUrl, String publishedAt, boolean demo) {
        this.id = id; this.title = title; this.summary = summary; this.body = body; this.imageUrl = imageUrl; this.publishedAt = publishedAt; this.demo = demo;
    }
}

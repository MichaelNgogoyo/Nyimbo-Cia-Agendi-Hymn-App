package org.nyimbo.agendi.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "hymns", indices = {@Index(value = {"version", "number"}, unique = true)})
public class HymnEntity {
    @PrimaryKey public long id;
    public int number;
    /** 0 = Old version, 1 = New version, 2 = imported source directory. */
    public int version;
    public String title;
    public String lyrics;
    public String musicKey;
    public String sourceUrl;
    public Integer crossReferenceNumber;
    public String notationUrl;
    public boolean demo;
    public boolean favorite;
    public String userNotes;
    public long lastViewedAt;

    public HymnEntity(int number, int version, String title, String lyrics, String musicKey,
                      Integer crossReferenceNumber, String notationUrl, boolean demo,
                      boolean favorite, String userNotes, long lastViewedAt, String sourceUrl) {
        this.id = version * 100000L + number;
        this.number = number; this.version = version; this.title = title; this.lyrics = lyrics;
        this.musicKey = musicKey; this.crossReferenceNumber = crossReferenceNumber;
        this.notationUrl = notationUrl; this.demo = demo; this.favorite = favorite;
        this.userNotes = userNotes; this.lastViewedAt = lastViewedAt; this.sourceUrl = sourceUrl;
    }

    /** Imported songs keep the source list order as their display number. */
    public int displayNumber() {
        return number;
    }
}

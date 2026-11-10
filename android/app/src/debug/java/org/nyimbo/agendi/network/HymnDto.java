package org.nyimbo.agendi.network;

import org.nyimbo.agendi.model.HymnEntity;

public class HymnDto {
    public int number, version;
    public String title, lyrics, musicKey, notationUrl;
    public Integer crossReferenceNumber;
    public boolean demo;
    public HymnEntity toEntity() { return new HymnEntity(number, version, title, lyrics, musicKey,
        crossReferenceNumber, notationUrl, demo, false, "", 0, null); }
}

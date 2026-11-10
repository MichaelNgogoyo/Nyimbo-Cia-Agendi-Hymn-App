package org.nyimbo.agendi.content;

public record HymnImportRecord(int version, int number, String title, String lyrics, String musicKey,
                               Integer crossReferenceNumber, String notationUrl, String contentLicense,
                               String licenseReference, boolean verified) {}

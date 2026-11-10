package org.nyimbo.agendi.content;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class HymnImportValidator {
    private HymnImportValidator() {}
    public static void validate(List<HymnImportRecord> rows) {
        if (rows == null || rows.isEmpty()) throw new IllegalArgumentException("Import must contain at least one hymn.");
        if (rows.size() > 5000) throw new IllegalArgumentException("An import may contain at most 5,000 hymn records.");
        Set<String> numbers = new HashSet<>();
        long totalCharacters = 0;
        for (HymnImportRecord row : rows) {
            if (row == null || (row.version() != 0 && row.version() != 1) || row.number() < 1) throw new IllegalArgumentException("Each hymn needs version 0 or 1 and a positive number.");
            if (blank(row.title()) || blank(row.lyrics())) throw new IllegalArgumentException("Hymn title and complete lyrics are required.");
            if (blank(row.contentLicense()) || blank(row.licenseReference())) throw new IllegalArgumentException("Every hymn needs a license name and license reference.");
            if (!row.verified()) throw new IllegalArgumentException("Only verified, authorized hymn records may be published to the app.");
            if (row.notationUrl() != null && !row.notationUrl().isBlank() && !row.notationUrl().startsWith("https://")) throw new IllegalArgumentException("Notation references must use HTTPS.");
            if (row.crossReferenceNumber() != null && row.crossReferenceNumber() < 1) throw new IllegalArgumentException("Cross-reference numbers must be positive.");
            if (!numbers.add(row.version() + ":" + row.number())) throw new IllegalArgumentException("Duplicate hymn version and number in import.");
            if (row.title().length() > 240 || row.lyrics().length() > 100000 || row.contentLicense().length() > 200 || row.licenseReference().length() > 500 || (row.musicKey() != null && row.musicKey().length() > 80) || (row.notationUrl() != null && row.notationUrl().length() > 500)) throw new IllegalArgumentException("A hymn field exceeds the supported length.");
            totalCharacters += row.title().length() + row.lyrics().length() + row.contentLicense().length() + row.licenseReference().length();
            if (totalCharacters > 10_000_000) throw new IllegalArgumentException("An import may contain at most 10 million characters.");
        }
    }
    private static boolean blank(String text) { return text == null || text.isBlank(); }
}

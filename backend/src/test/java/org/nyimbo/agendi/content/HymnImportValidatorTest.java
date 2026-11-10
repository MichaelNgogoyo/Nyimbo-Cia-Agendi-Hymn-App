package org.nyimbo.agendi.content;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HymnImportValidatorTest {
    private HymnImportRecord authorized(boolean verified, String license) {
        return new HymnImportRecord(0, 12, "Verified title", "Verse text", null, null, null, license, "https://example.org/license", verified);
    }
    @Test void acceptsVerifiedLicensedContent() { assertDoesNotThrow(() -> HymnImportValidator.validate(List.of(authorized(true, "Church license")))); }
    @Test void rejectsUnverifiedContent() { assertThrows(IllegalArgumentException.class, () -> HymnImportValidator.validate(List.of(authorized(false, "Church license")))); }
    @Test void rejectsMissingLicense() { assertThrows(IllegalArgumentException.class, () -> HymnImportValidator.validate(List.of(authorized(true, " ")))); }
    @Test void rejectsDuplicateVersionNumber() {
        assertThrows(IllegalArgumentException.class, () -> HymnImportValidator.validate(List.of(authorized(true, "License"), authorized(true, "License"))));
    }
}

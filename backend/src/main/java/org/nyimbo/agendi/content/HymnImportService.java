package org.nyimbo.agendi.content;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service public class HymnImportService {
    private final HymnRepository repository;
    public HymnImportService(HymnRepository repository) { this.repository = repository; }
    @Transactional public List<HymnEntity> importVerified(List<HymnImportRecord> rows) {
        HymnImportValidator.validate(rows);
        List<HymnEntity> saved = new ArrayList<>();
        for (HymnImportRecord row : rows) {
            HymnEntity hymn = repository.findByVersionAndNumber(row.version(), row.number()).orElseGet(HymnEntity::new);
            hymn.version = row.version(); hymn.number = row.number(); hymn.title = row.title().trim(); hymn.lyrics = row.lyrics().trim();
            hymn.musicKey = row.musicKey(); hymn.crossReferenceNumber = row.crossReferenceNumber(); hymn.notationUrl = row.notationUrl();
            hymn.contentLicense = row.contentLicense().trim(); hymn.licenseReference = row.licenseReference().trim(); hymn.verified = true; hymn.demo = false;
            saved.add(repository.save(hymn));
        }
        for (HymnImportRecord row : rows) if (row.crossReferenceNumber() != null) {
            int targetVersion = row.version() == 0 ? 1 : 0;
            HymnEntity target = repository.findByVersionAndNumber(targetVersion, row.crossReferenceNumber()).orElse(null);
            if (target == null || !target.verified) throw new IllegalArgumentException("Cross-reference target " + row.crossReferenceNumber() + " must exist as a verified hymn in the other version.");
        }
        return saved;
    }
}

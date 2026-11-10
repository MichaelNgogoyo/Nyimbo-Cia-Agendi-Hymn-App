package org.nyimbo.agendi.content;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HymnRepository extends JpaRepository<HymnEntity, Long> {
    List<HymnEntity> findByVersionAndVerifiedTrueOrderByNumberAsc(int version);
    Optional<HymnEntity> findByVersionAndNumber(int version, int number);
}

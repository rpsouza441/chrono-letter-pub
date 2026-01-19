package br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.adapter.out.persistence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface LetterJpaRepository extends JpaRepository<LetterEntity, UUID> {
    List<LetterEntity> findAllByUserId(UUID userId);

    List<LetterEntity> findByUserIdAndStatus(UUID userId, LetterStatusEntity status);

    @Query("SELECT l FROM LetterEntity l WHERE l.scheduledDate <= :dateLimit AND l.sendAt IS NULL AND l.status = 'PENDING'")
    List<LetterEntity> findPendingForMaterialization(@Param("dateLimit") LocalDate dateLimit);

    @Query("SELECT l FROM LetterEntity l WHERE l.status = 'PENDING' AND l.sendAt <= :now ORDER BY l.sendAt ASC")
    List<LetterEntity> findReadyToSend(@Param("now") Instant now, org.springframework.data.domain.Pageable pageable);

    void deleteAllByUserId(UUID userId);
}

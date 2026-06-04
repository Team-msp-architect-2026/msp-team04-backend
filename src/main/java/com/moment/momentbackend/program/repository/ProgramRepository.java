package com.moment.momentbackend.program.repository;

import com.moment.momentbackend.program.entity.Institution;
import com.moment.momentbackend.program.entity.Program;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    @Query("SELECT p FROM Program p " +
            "LEFT JOIN FETCH p.institution " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.id = :id AND p.isPublic = true")
    Optional<Program> findDetailById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Program p WHERE p.id = :id")
    Optional<Program> findByIdForUpdate(@Param("id") Long id);

    long countByIsFreeTrueAndIsPublicTrue();

    @Query("SELECT p FROM Program p WHERE p.category IN :categories AND p.isRecruiting = true AND p.isPublic = true ORDER BY p.ratingAvg DESC")
    List<Program> findComplementaryPrograms(@Param("categories") List<String> categories);

    @Query("SELECT p FROM Program p WHERE p.isRecruiting = true AND p.isPublic = true AND p.category != :category ORDER BY p.ratingAvg DESC")
    List<Program> findOtherRecruitingPrograms(@Param("category") String category);

    Optional<Program> findByExternalSourceAndExternalId(
            String externalSource, String externalId);

    @Query("SELECT p FROM Program p WHERE p.isPublic = true AND p.isRecruiting = true AND p.deadlineDate = :deadlineDate")
    List<Program> findRecruitingProgramsByDeadlineDate(@Param("deadlineDate") LocalDate deadlineDate);

    @Query("SELECT p FROM Program p WHERE p.isPublic = true AND p.isRecruiting = true")
    List<Program> findRecruitingProgramsForNotification();

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE program p
            SET institution_id = i.id,
                updated_at = NOW()
            FROM institution i
            WHERE p.external_source = :externalSource
              AND i.external_source = :externalSource
              AND i.external_id = CONCAT('INST_', p.external_id)
              AND (p.institution_id IS NULL OR p.institution_id <> i.id)
            """, nativeQuery = true)
    int backfillInstitutionLinksByExternalSource(@Param("externalSource") String externalSource);
}

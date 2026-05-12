package com.moment.momentbackend.program.repository;

import com.moment.momentbackend.program.entity.Program;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
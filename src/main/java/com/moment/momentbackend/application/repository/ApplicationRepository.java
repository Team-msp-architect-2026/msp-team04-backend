package com.moment.momentbackend.application.repository;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.type.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserIdAndChildIdAndProgramIdAndApplicationStatusIn(
            Long userId,
            Long childId,
            Long programId,
            Collection<ApplicationStatus> statuses
    );

    Optional<Application> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT a FROM Application a
            WHERE a.applicationStatus IN :statuses
            AND a.seatLockedUntil IS NOT NULL
            AND a.seatLockedUntil < :now
            """)
    List<Application> findExpiredApplications(
            @Param("statuses") List<ApplicationStatus> statuses,
            @Param("now") LocalDateTime now
    );

    List<Application> findByUserIdOrderByAppliedAtDesc(Long userId);

    List<Application> findByUserIdAndApplicationStatusOrderByAppliedAtDesc(
            Long userId,
            ApplicationStatus applicationStatus
    );
}

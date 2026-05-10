package com.moment.momentbackend.application.repository;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.type.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserIdAndChildIdAndProgramIdAndApplicationStatusIn(
            Long userId,
            Long childId,
            Long programId,
            Collection<ApplicationStatus> statuses
    );
}
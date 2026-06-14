package com.moment.momentbackend.benefit.repository;

import com.moment.momentbackend.benefit.entity.BenefitAssessmentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BenefitAssessmentProfileRepository extends JpaRepository<BenefitAssessmentProfile, Long> {

    Optional<BenefitAssessmentProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}

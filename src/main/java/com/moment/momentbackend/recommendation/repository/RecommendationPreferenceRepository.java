package com.moment.momentbackend.recommendation.repository;

import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationPreferenceRepository extends JpaRepository<RecommendationPreference, Long> {
    Optional<RecommendationPreference> findByIdAndUserId(Long id, Long userId);
    Optional<RecommendationPreference> findTopByChildIdOrderByCreatedAtDesc(Long childId);
}
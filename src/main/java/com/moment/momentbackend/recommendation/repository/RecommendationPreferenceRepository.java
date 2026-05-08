package com.moment.momentbackend.recommendation.repository;

import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationPreferenceRepository extends JpaRepository<RecommendationPreference, Long> {
}
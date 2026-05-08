package com.moment.momentbackend.recommendation.repository;

import com.moment.momentbackend.recommendation.entity.AiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {
    void deleteAllByPreferenceId(Long preferenceId);
}
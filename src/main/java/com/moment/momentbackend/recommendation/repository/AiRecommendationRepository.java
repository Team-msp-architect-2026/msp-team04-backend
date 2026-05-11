package com.moment.momentbackend.recommendation.repository;

import com.moment.momentbackend.recommendation.entity.AiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {

    void deleteAllByPreferenceId(Long preferenceId);

    List<AiRecommendation> findAllByUserIdAndChildIdOrderByCreatedAtDescRankNoAsc(Long userId, Long childId);

    List<AiRecommendation> findAllByUserIdAndPreferenceIdOrderByRankNoAsc(Long userId, Long preferenceId);

    List<AiRecommendation> findAllByUserIdAndPreferenceIdAndIsTop3TrueOrderByRankNoAsc(Long userId, Long preferenceId);

    boolean existsByPreferenceIdAndProgramId(Long preferenceId, Long programId);

    boolean existsByPreferenceIdAndRankNo(Long preferenceId, Integer rankNo);

    long countByChildId(Long childId);
}
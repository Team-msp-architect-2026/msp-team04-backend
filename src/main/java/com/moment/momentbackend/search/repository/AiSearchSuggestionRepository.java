package com.moment.momentbackend.search.repository;

import com.moment.momentbackend.search.entity.AiSearchSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiSearchSuggestionRepository extends JpaRepository<AiSearchSuggestion, Long> {

    List<AiSearchSuggestion> findTop20ByUserIdAndIsGlobalFalseOrderByCreatedAtDesc(Long userId);

    List<AiSearchSuggestion> findTop20ByIsGlobalTrueOrderByCreatedAtDesc();
}

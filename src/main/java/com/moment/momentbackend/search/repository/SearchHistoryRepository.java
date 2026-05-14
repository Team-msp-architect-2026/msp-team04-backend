package com.moment.momentbackend.search.repository;

import com.moment.momentbackend.search.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findTop30ByUserIdOrderBySearchedAtDesc(Long userId);

    void deleteByUserIdAndKeywordIgnoreCase(Long userId, String keyword);

    void deleteByUserId(Long userId);
}

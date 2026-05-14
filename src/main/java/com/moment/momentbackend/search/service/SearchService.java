package com.moment.momentbackend.search.service;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.search.dto.RecentSearchResponse;
import com.moment.momentbackend.search.dto.SearchProgramResponse;
import com.moment.momentbackend.search.entity.SearchHistory;
import com.moment.momentbackend.search.repository.SearchHistoryRepository;
import com.moment.momentbackend.search.repository.SearchProgramQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int MAX_KEYWORD_LENGTH = 255;
    private static final int RECENT_SEARCH_LIMIT = 10;

    private final SearchProgramQueryRepository searchProgramQueryRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @Transactional
    public Page<SearchProgramResponse> searchPrograms(Long userId, String keyword, Pageable pageable) {
        String normalizedKeyword = normalizeKeyword(keyword);

        if (userId != null) {
            saveRecentKeyword(userId, normalizedKeyword);
        }

        return searchProgramQueryRepository.searchPrograms(normalizedKeyword, pageable)
                .map(SearchProgramResponse::new);
    }

    @Transactional(readOnly = true)
    public List<RecentSearchResponse> getRecentSearches(Long userId) {
        validateUserId(userId);

        Set<String> seenKeywords = new LinkedHashSet<>();

        return searchHistoryRepository.findTop30ByUserIdOrderBySearchedAtDesc(userId).stream()
                .filter(history -> seenKeywords.add(history.getKeyword().toLowerCase()))
                .limit(RECENT_SEARCH_LIMIT)
                .map(RecentSearchResponse::new)
                .toList();
    }

    @Transactional
    public void deleteRecentSearches(Long userId) {
        validateUserId(userId);
        searchHistoryRepository.deleteByUserId(userId);
    }

    private void saveRecentKeyword(Long userId, String keyword) {
        searchHistoryRepository.deleteByUserIdAndKeywordIgnoreCase(userId, keyword);
        searchHistoryRepository.save(SearchHistory.create(userId, keyword));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        String normalizedKeyword = keyword.trim().replaceAll("\\s+", " ");

        if (normalizedKeyword.length() > MAX_KEYWORD_LENGTH) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        return normalizedKeyword;
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}

package com.moment.momentbackend.search.service;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.search.dto.AiSearchSuggestionResponse;
import com.moment.momentbackend.search.dto.RecentSearchResponse;
import com.moment.momentbackend.search.dto.SearchProgramResponse;
import com.moment.momentbackend.search.entity.AiSearchSuggestion;
import com.moment.momentbackend.search.entity.SearchHistory;
import com.moment.momentbackend.search.repository.AiSearchSuggestionRepository;
import com.moment.momentbackend.search.repository.SearchHistoryRepository;
import com.moment.momentbackend.search.repository.SearchProgramQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int MAX_KEYWORD_LENGTH = 255;
    private static final int RECENT_SEARCH_LIMIT = 10;
    private static final int SEARCH_SUGGESTION_LIMIT = 10;
    private static final int RECENT_SUGGESTION_LIMIT = 5;
    private static final int PERSONAL_SUGGESTION_LIMIT = 3;

    private final SearchProgramQueryRepository searchProgramQueryRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final AiSearchSuggestionRepository aiSearchSuggestionRepository;

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

    @Transactional(readOnly = true)
    public List<AiSearchSuggestionResponse> getSearchSuggestions(Long userId) {
        validateUserId(userId);

        Map<String, AiSearchSuggestionResponse> suggestions = new LinkedHashMap<>();

        addRecentSearchSuggestions(userId, suggestions);
        addPersonalAiSuggestions(userId, suggestions);
        addGlobalAiSuggestions(suggestions);

        return suggestions.values().stream()
                .limit(SEARCH_SUGGESTION_LIMIT)
                .toList();
    }

    @Transactional
    public void deleteRecentSearches(Long userId) {
        validateUserId(userId);
        searchHistoryRepository.deleteByUserId(userId);
    }

    private void addRecentSearchSuggestions(
            Long userId,
            Map<String, AiSearchSuggestionResponse> suggestions
    ) {
        int addedCount = 0;

        for (SearchHistory history : searchHistoryRepository.findTop30ByUserIdOrderBySearchedAtDesc(userId)) {
            if (addedCount >= RECENT_SUGGESTION_LIMIT) {
                return;
            }

            boolean added = addSuggestion(
                    suggestions,
                    history.getKeyword(),
                    AiSearchSuggestionResponse.fromRecentSearch(history)
            );

            if (added) {
                addedCount++;
            }
        }
    }

    private void addPersonalAiSuggestions(
            Long userId,
            Map<String, AiSearchSuggestionResponse> suggestions
    ) {
        int addedCount = 0;

        for (AiSearchSuggestion suggestion : aiSearchSuggestionRepository.findTop20ByUserIdAndIsGlobalFalseOrderByCreatedAtDesc(userId)) {
            if (suggestions.size() >= SEARCH_SUGGESTION_LIMIT || addedCount >= PERSONAL_SUGGESTION_LIMIT) {
                return;
            }

            boolean added = addSuggestion(
                    suggestions,
                    suggestion.getSuggestionText(),
                    AiSearchSuggestionResponse.fromPersonalSuggestion(suggestion)
            );

            if (added) {
                addedCount++;
            }
        }
    }

    private void addGlobalAiSuggestions(
            Map<String, AiSearchSuggestionResponse> suggestions
    ) {
        for (AiSearchSuggestion suggestion : aiSearchSuggestionRepository.findTop20ByIsGlobalTrueOrderByCreatedAtDesc()) {
            if (suggestions.size() >= SEARCH_SUGGESTION_LIMIT) {
                return;
            }

            addSuggestion(
                    suggestions,
                    suggestion.getSuggestionText(),
                    AiSearchSuggestionResponse.fromGlobalSuggestion(suggestion)
            );
        }
    }

    private boolean addSuggestion(
            Map<String, AiSearchSuggestionResponse> suggestions,
            String keyword,
            AiSearchSuggestionResponse response
    ) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }

        String key = normalizeSuggestionKey(keyword);

        if (suggestions.containsKey(key)) {
            return false;
        }

        suggestions.put(key, response);
        return true;
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

    private String normalizeSuggestionKey(String keyword) {
        return keyword.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}

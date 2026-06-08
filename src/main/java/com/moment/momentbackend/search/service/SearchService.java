package com.moment.momentbackend.search.service;

import com.moment.momentbackend.child.entity.ChildConcern;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildConcernRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.global.metrics.BusinessMetricsService;
import com.moment.momentbackend.search.client.SearchSuggestionAiClient;
import com.moment.momentbackend.search.dto.AiSearchSuggestionResponse;
import com.moment.momentbackend.search.dto.RecentSearchResponse;
import com.moment.momentbackend.search.dto.SearchProgramResponse;
import com.moment.momentbackend.search.dto.SearchSuggestionAiChildRequest;
import com.moment.momentbackend.search.dto.SearchSuggestionAiRequest;
import com.moment.momentbackend.search.dto.SearchSuggestionAiResponse;
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

import java.time.LocalDate;
import java.time.Period;
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
    private static final int SEARCH_SUGGESTION_LIMIT = 5;
    private static final int RECENT_SUGGESTION_LIMIT = 3;
    private static final int PERSONAL_SUGGESTION_LIMIT = 2;

    private final SearchProgramQueryRepository searchProgramQueryRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final AiSearchSuggestionRepository aiSearchSuggestionRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ChildConcernRepository childConcernRepository;
    private final SearchSuggestionAiClient searchSuggestionAiClient;
    private final BusinessMetricsService businessMetricsService;

    @Transactional
    public Page<SearchProgramResponse> searchPrograms(Long userId, String keyword, Pageable pageable) {
        return businessMetricsService.recordSearch(
                "basic",
                () -> searchProgramsInternal(userId, keyword, pageable)
        );
    }

    private Page<SearchProgramResponse> searchProgramsInternal(Long userId, String keyword, Pageable pageable) {
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
        return businessMetricsService.recordSearch(
                "suggestion",
                () -> getSearchSuggestionsInternal(userId)
        );
    }

    private List<AiSearchSuggestionResponse> getSearchSuggestionsInternal(Long userId) {
        validateUserId(userId);

        List<String> recentKeywords = getRecentKeywords(userId);
        List<String> globalKeywords = getGlobalKeywords();
        List<SearchSuggestionAiChildRequest> children = getChildContexts(userId);

        SearchSuggestionAiRequest aiRequest = new SearchSuggestionAiRequest(
                userId,
                children,
                recentKeywords,
                globalKeywords,
                SEARCH_SUGGESTION_LIMIT
        );

        List<AiSearchSuggestionResponse> aiResponses = searchSuggestionAiClient
                .generateSuggestions(aiRequest)
                .map(SearchSuggestionAiResponse::getSuggestions)
                .map(this::toGeneratedSuggestionResponses)
                .orElse(List.of());

        if (!aiResponses.isEmpty()) {
            businessMetricsService.recordSearchSource("suggestion", "ai");
            return aiResponses;
        }

        businessMetricsService.recordSearchSource("suggestion", "fallback");
        return getFallbackSearchSuggestions(userId);
    }

    @Transactional
    public void deleteRecentSearches(Long userId) {
        validateUserId(userId);
        searchHistoryRepository.deleteByUserId(userId);
    }

    private List<AiSearchSuggestionResponse> toGeneratedSuggestionResponses(List<String> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            return List.of();
        }

        Map<String, AiSearchSuggestionResponse> deduplicated = new LinkedHashMap<>();

        for (String suggestion : suggestions) {
            if (deduplicated.size() >= SEARCH_SUGGESTION_LIMIT) {
                break;
            }

            if (suggestion == null || suggestion.isBlank()) {
                continue;
            }

            String normalized = suggestion.trim().replaceAll("\\s+", " ");
            String key = normalizeSuggestionKey(normalized);

            if (!deduplicated.containsKey(key)) {
                deduplicated.put(key, AiSearchSuggestionResponse.fromGeneratedKeyword(normalized));
            }
        }

        return deduplicated.values().stream()
                .limit(SEARCH_SUGGESTION_LIMIT)
                .toList();
    }

    private List<AiSearchSuggestionResponse> getFallbackSearchSuggestions(Long userId) {
        Map<String, AiSearchSuggestionResponse> suggestions = new LinkedHashMap<>();

        addRecentSearchSuggestions(userId, suggestions);
        addPersonalAiSuggestions(userId, suggestions);
        addGlobalAiSuggestions(suggestions);

        return suggestions.values().stream()
                .limit(SEARCH_SUGGESTION_LIMIT)
                .toList();
    }

    private List<String> getRecentKeywords(Long userId) {
        Set<String> seenKeywords = new LinkedHashSet<>();

        return searchHistoryRepository.findTop30ByUserIdOrderBySearchedAtDesc(userId).stream()
                .filter(history -> seenKeywords.add(normalizeSuggestionKey(history.getKeyword())))
                .limit(RECENT_SUGGESTION_LIMIT)
                .map(SearchHistory::getKeyword)
                .toList();
    }

    private List<String> getGlobalKeywords() {
        Set<String> seenKeywords = new LinkedHashSet<>();

        return aiSearchSuggestionRepository.findTop20ByIsGlobalTrueOrderByCreatedAtDesc().stream()
                .map(AiSearchSuggestion::getSuggestionText)
                .filter(keyword -> keyword != null && !keyword.isBlank())
                .filter(keyword -> seenKeywords.add(normalizeSuggestionKey(keyword)))
                .limit(SEARCH_SUGGESTION_LIMIT)
                .toList();
    }

    private List<SearchSuggestionAiChildRequest> getChildContexts(Long userId) {
        return childProfileRepository.findAllByUserId(userId).stream()
                .map(child -> new SearchSuggestionAiChildRequest(
                        child.getId(),
                        child.getChildName(),
                        calculateAge(child),
                        getConcerns(child)
                ))
                .toList();
    }

    private Integer calculateAge(ChildProfile child) {
        if (child.getBirthDate() == null) {
            return null;
        }

        return Period.between(child.getBirthDate(), LocalDate.now()).getYears();
    }

    private List<String> getConcerns(ChildProfile child) {
        return childConcernRepository.findByChildProfileId(child.getId()).stream()
                .map(ChildConcern::getConcern)
                .filter(concern -> concern != null && !concern.isBlank())
                .toList();
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

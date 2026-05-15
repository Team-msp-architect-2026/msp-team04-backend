package com.moment.momentbackend.search.dto;

import com.moment.momentbackend.search.entity.AiSearchSuggestion;
import com.moment.momentbackend.search.entity.SearchHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiSearchSuggestionResponse {

    private final Long id;
    private final String keyword;
    private final String source;
    private final LocalDateTime createdAt;

    private AiSearchSuggestionResponse(Long id, String keyword, String source, LocalDateTime createdAt) {
        this.id = id;
        this.keyword = keyword;
        this.source = source;
        this.createdAt = createdAt;
    }

    public static AiSearchSuggestionResponse fromGeneratedKeyword(String keyword) {
        return new AiSearchSuggestionResponse(
                null,
                keyword,
                "PERSONAL",
                LocalDateTime.now()
        );
    }

    public static AiSearchSuggestionResponse fromRecentSearch(SearchHistory searchHistory) {
        return new AiSearchSuggestionResponse(
                searchHistory.getId(),
                searchHistory.getKeyword(),
                "RECENT",
                searchHistory.getSearchedAt()
        );
    }

    public static AiSearchSuggestionResponse fromPersonalSuggestion(AiSearchSuggestion suggestion) {
        return new AiSearchSuggestionResponse(
                suggestion.getId(),
                suggestion.getSuggestionText(),
                "PERSONAL",
                suggestion.getCreatedAt()
        );
    }

    public static AiSearchSuggestionResponse fromGlobalSuggestion(AiSearchSuggestion suggestion) {
        return new AiSearchSuggestionResponse(
                suggestion.getId(),
                suggestion.getSuggestionText(),
                "GLOBAL",
                suggestion.getCreatedAt()
        );
    }
}

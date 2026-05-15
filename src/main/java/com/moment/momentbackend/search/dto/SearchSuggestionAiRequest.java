package com.moment.momentbackend.search.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchSuggestionAiRequest {

    private final Long userId;
    private final List<SearchSuggestionAiChildRequest> children;
    private final List<String> recentSearches;
    private final List<String> globalKeywords;
    private final int limit;

    public SearchSuggestionAiRequest(
            Long userId,
            List<SearchSuggestionAiChildRequest> children,
            List<String> recentSearches,
            List<String> globalKeywords,
            int limit
    ) {
        this.userId = userId;
        this.children = children;
        this.recentSearches = recentSearches;
        this.globalKeywords = globalKeywords;
        this.limit = limit;
    }
}

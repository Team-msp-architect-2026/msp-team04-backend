package com.moment.momentbackend.search.dto;

import com.moment.momentbackend.search.entity.SearchHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RecentSearchResponse {

    private final Long id;
    private final String keyword;
    private final LocalDateTime searchedAt;

    public RecentSearchResponse(SearchHistory searchHistory) {
        this.id = searchHistory.getId();
        this.keyword = searchHistory.getKeyword();
        this.searchedAt = searchHistory.getSearchedAt();
    }
}

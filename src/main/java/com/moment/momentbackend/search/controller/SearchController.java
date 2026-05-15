package com.moment.momentbackend.search.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.search.dto.AiSearchSuggestionResponse;
import com.moment.momentbackend.search.dto.RecentSearchResponse;
import com.moment.momentbackend.search.dto.SearchProgramResponse;
import com.moment.momentbackend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ApiResponse<Page<SearchProgramResponse>> searchPrograms(
            @AuthenticationPrincipal Long userId,
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "deadlineDate", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.ok(searchService.searchPrograms(userId, keyword, pageable));
    }

    @GetMapping("/recent")
    public ApiResponse<List<RecentSearchResponse>> getRecentSearches(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.ok(searchService.getRecentSearches(userId));
    }

    @GetMapping("/suggestions")
    public ApiResponse<List<AiSearchSuggestionResponse>> getSearchSuggestions(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.ok(searchService.getSearchSuggestions(userId));
    }

    @DeleteMapping("/recent")
    public ApiResponse<Void> deleteRecentSearches(
            @AuthenticationPrincipal Long userId
    ) {
        searchService.deleteRecentSearches(userId);
        return ApiResponse.ok(null, "최근 검색어가 삭제되었습니다.");
    }
}

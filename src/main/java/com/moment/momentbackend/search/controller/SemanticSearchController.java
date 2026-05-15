package com.moment.momentbackend.search.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.search.dto.SemanticSearchResponseDto;
import com.moment.momentbackend.search.service.SemanticSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;

    @GetMapping("/semantic")
    public ApiResponse<List<SemanticSearchResponseDto>> semanticSearch(
            @RequestParam("q") String q
    ) {
        return ApiResponse.ok(semanticSearchService.search(q));
    }
}

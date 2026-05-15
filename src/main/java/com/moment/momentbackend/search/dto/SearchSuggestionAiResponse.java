package com.moment.momentbackend.search.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SearchSuggestionAiResponse {

    private List<String> suggestions;
    private String source;
}

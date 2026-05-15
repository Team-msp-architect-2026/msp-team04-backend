package com.moment.momentbackend.search.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchSuggestionAiChildRequest {

    private final Long childId;
    private final String name;
    private final Integer age;
    private final List<String> concerns;

    public SearchSuggestionAiChildRequest(
            Long childId,
            String name,
            Integer age,
            List<String> concerns
    ) {
        this.childId = childId;
        this.name = name;
        this.age = age;
        this.concerns = concerns;
    }
}

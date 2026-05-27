package com.moment.momentbackend.review.dto;

import java.util.List;

public record ReviewKeywordAiResponse(
        List<String> positiveKeywords,
        List<String> negativeKeywords,
        String summary,
        String source
) {
}

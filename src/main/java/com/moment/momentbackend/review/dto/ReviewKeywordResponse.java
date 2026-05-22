package com.moment.momentbackend.review.dto;

import java.util.List;

public record ReviewKeywordResponse(
        Long programId,
        int reviewCount,
        Double ratingAverage,
        List<String> positiveKeywords,
        List<String> negativeKeywords,
        String summary,
        String source
) {
}

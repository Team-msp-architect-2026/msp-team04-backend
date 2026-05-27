package com.moment.momentbackend.review.dto;

import java.util.List;
import java.util.Map;

public record ReviewKeywordStatsRequest(
        int reviewCount,
        Double ratingAverage,
        Map<String, Integer> ratingDistribution,
        List<String> positiveKeywords,
        List<String> negativeKeywords,
        List<String> reviewTexts
) {
}

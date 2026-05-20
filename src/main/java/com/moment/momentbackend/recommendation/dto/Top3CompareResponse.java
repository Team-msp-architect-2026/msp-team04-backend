package com.moment.momentbackend.recommendation.dto;

import java.util.List;

public record Top3CompareResponse(
        String commonSummary,
        List<Top3CompareItemResponse> items,
        String source
) {
}

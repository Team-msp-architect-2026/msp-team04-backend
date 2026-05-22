package com.moment.momentbackend.review.dto;

public record ReviewKeywordAiRequest(
        ReviewKeywordProgramRequest program,
        ReviewKeywordStatsRequest stats
) {
}

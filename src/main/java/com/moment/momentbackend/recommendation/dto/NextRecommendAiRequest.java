package com.moment.momentbackend.recommendation.dto;

import java.util.List;

public record NextRecommendAiRequest(
        NextRecommendChildRequest child,
        NextRecommendAppliedProgramRequest appliedProgram,
        List<NextRecommendCandidateRequest> candidates
) {
}

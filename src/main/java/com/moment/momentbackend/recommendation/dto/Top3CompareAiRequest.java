package com.moment.momentbackend.recommendation.dto;

import java.util.List;

public record Top3CompareAiRequest(
        Top3CompareChildRequest child,
        Top3ComparePreferenceRequest preference,
        List<Top3CompareProgramRequest> programs
) {
}

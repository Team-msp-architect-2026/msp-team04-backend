package com.moment.momentbackend.recommendation.dto;

public record ProgramReasonAiRequest(
        ProgramReasonChildRequest child,
        ProgramReasonPreferenceRequest preference,
        ProgramReasonProgramRequest program,
        ProgramReasonScoreRequest score
) {
}

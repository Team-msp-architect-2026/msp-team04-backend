package com.moment.momentbackend.search.dto;

import java.util.List;

public record RerankRequest(
        String query,
        List<RerankCandidateRequest> candidates
) {
}

package com.moment.momentbackend.search.dto;

import java.util.List;

public record RerankResponse(
        List<RerankResult> results,
        String source
) {
}

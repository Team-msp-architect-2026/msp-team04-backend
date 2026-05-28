package com.moment.momentbackend.batch.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.moment.momentbackend.benefit.entity.BenefitMaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GovernmentBenefitParser {

    private static final String EXTERNAL_SOURCE = "GOVERNMENT_BENEFIT";

    public List<BenefitMaster> parse(JsonNode root) {
        List<BenefitMaster> results = new ArrayList<>();
        JsonNode dataArray = root.path("data");

        if (!dataArray.isArray()) {
            log.warn("government_benefit: data 배열 없음");
            return results;
        }

        for (JsonNode row : dataArray) {
            try {
                BenefitMaster benefit = BenefitMaster.builder()
                        .externalSource(EXTERNAL_SOURCE)
                        .externalId(row.path("서비스ID").asText(null))
                        .benefitName(row.path("서비스명").asText(null))
                        .benefitType(mapBenefitType(row.path("지원유형").asText(null)))
                        .supportDescription(truncate(row.path("지원내용").asText(null), 500))
                        .conditionDescription(row.path("지원대상").asText(null))
                        .applyLink(row.path("상세조회URL").asText(null))
                        .region(truncate(row.path("소관기관명").asText(null), 100))
                        .isActive(true)
                        .lastSyncedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build();
                results.add(benefit);
            } catch (Exception e) {
                log.warn("benefit 파싱 실패 row: {}", e.getMessage());
            }
        }
        log.info("government_benefit 파싱 완료: {}건", results.size());
        return results;
    }

    private String mapBenefitType(String raw) {
        if (raw == null) return null;
        return switch (raw) {
            case "현금" -> "ALLOWANCE";
            case "현물" -> "VOUCHER";
            case "서비스" -> "FREE_SERVICE";
            case "감면" -> "DISCOUNT";
            default -> null;
        };
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
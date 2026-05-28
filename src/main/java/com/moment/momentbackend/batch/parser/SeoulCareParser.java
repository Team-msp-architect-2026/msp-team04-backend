package com.moment.momentbackend.batch.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.moment.momentbackend.program.entity.Institution;
import com.moment.momentbackend.program.entity.Program;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class SeoulCareParser {

    private static final String EXTERNAL_SOURCE = "SEOUL_CARE";

    private static final Map<String, String> ROOT_KEY_MAP = Map.of(
            "wooridongne_kium",       "TnFcltySttusInfo1001",
            "joint_childcare_room",   "TnFcltySttusInfo1002",
            "local_child_center",     "TnFcltySttusInfo1003",
            "joint_childcare_sharing","TnFcltySttusInfo1004"
    );

    public record ParseResult(List<Institution> institutions, List<Program> programs) {}

    public ParseResult parse(JsonNode root, String sourceDetail) {
        String rootKey = ROOT_KEY_MAP.getOrDefault(sourceDetail, "");
        JsonNode rowArray = root.path(rootKey).path("row");

        List<Institution> institutions = new ArrayList<>();
        List<Program> programs = new ArrayList<>();

        if (!rowArray.isArray()) {
            log.warn("seoul_care/{}: row 배열 없음", sourceDetail);
            return new ParseResult(institutions, programs);
        }

        for (JsonNode row : rowArray) {
            try {
                String externalId = row.path("FCLTY_CD").asText(null);
                if (externalId == null) externalId = row.path("INST_CD").asText(null);
                if (externalId == null) externalId = row.path("FCLTY_NM").asText("UNKNOWN");

                Institution institution = Institution.builder()
                        .institutionName(row.path("FCLTY_NM").asText("미상"))
                        .institutionType("PUBLIC")
                        .address(row.path("RDNMADR_NM").asText(row.path("LNMADR_NM").asText(null)))
                        .phone(row.path("TELNO").asText(null))
                        .latitude(parseBigDecimal(row.path("LATITUDE").asText(null)))
                        .longitude(parseBigDecimal(row.path("LONGITUDE").asText(null)))
                        .externalSource(EXTERNAL_SOURCE)
                        .externalId("INST_" + externalId)
                        .lastSyncedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build();
                institutions.add(institution);

                Program program = Program.builder()
                        .title(row.path("FCLTY_NM").asText("제목없음"))
                        .category(mapCategory(sourceDetail))
                        .region(row.path("SIGNGU_NM").asText(null))
                        .detailAddress(row.path("RDNMADR_NM").asText(null))
                        .price(0)
                        .isFree(true)
                        .contactPhone(row.path("TELNO").asText(null))
                        .latitude(parseBigDecimal(row.path("LATITUDE").asText(null)))
                        .longitude(parseBigDecimal(row.path("LONGITUDE").asText(null)))
                        .isRecruiting(true)
                        .isPublic(true)
                        .ratingAvg(BigDecimal.ZERO)
                        .reviewCount(0)
                        .externalSource(EXTERNAL_SOURCE)
                        .externalId(externalId)
                        .lastSyncedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build();
                programs.add(program);

            } catch (Exception e) {
                log.warn("seoul_care row 파싱 실패: {}", e.getMessage());
            }
        }
        log.info("seoul_care/{} 파싱 완료: {}건", sourceDetail, programs.size());
        return new ParseResult(institutions, programs);
    }

    private String mapCategory(String sourceDetail) {
        return "CARE";
    }

    private BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return new BigDecimal(raw); }
        catch (Exception e) { return null; }
    }
}
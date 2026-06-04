package com.moment.momentbackend.batch.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.moment.momentbackend.program.entity.Institution;
import com.moment.momentbackend.program.entity.Program;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SeoulAcademyParser {

    private static final String EXTERNAL_SOURCE = "SEOUL_ACADEMY";

    public record ParseResult(List<Institution> institutions, List<Program> programs) {}

    public ParseResult parse(JsonNode root) {
        JsonNode rowArray = root.path("neisAcademyInfo").path("row");
        List<Institution> institutions = new ArrayList<>();
        List<Program> programs = new ArrayList<>();

        if (!rowArray.isArray()) {
            log.warn("seoul_academy: row 배열 없음");
            return new ParseResult(institutions, programs);
        }

        for (JsonNode row : rowArray) {
            try {
                String dsgNo = row.path("PEI_DSGN_NO").asText(null);
                if (dsgNo == null) continue;

                String academyName = row.path("PEI_NM").asText("미상");
                String courseName = row.path("TRNG_CRS_NM").asText(null);
                String programTitle = buildProgramTitle(academyName, courseName);

                Institution institution = Institution.builder()
                        .institutionName(academyName)
                        .institutionType("PRIVATE")
                        .address(row.path("ROAD_NM_ADDR").asText(null))
                        .externalSource(EXTERNAL_SOURCE)
                        .externalId("INST_" + dsgNo)
                        .lastSyncedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build();
                institutions.add(institution);

                String priceRaw = row.path("INDV_ATNLC_AMT_CN").asText("0");
                int price = parsePrice(priceRaw);

                Program program = Program.builder()
                        .title(programTitle)
                        .category("EDUCATION")
                        .programType(null)
                        .region(row.path("ADMDST_NM").asText(null))
                        .detailAddress(row.path("DADDR").asText(null))
                        .curriculum(courseName)
                        .price(price)
                        .isFree(price == 0)
                        .maxCapacity(parseIntOrNull(row.path("PSCP_SUM").asText(null)))
                        .isRecruiting(true)
                        .isPublic(true)
                        .ratingAvg(BigDecimal.ZERO)
                        .reviewCount(0)
                        .externalSource(EXTERNAL_SOURCE)
                        .externalId(dsgNo)
                        .lastSyncedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build();
                programs.add(program);

            } catch (Exception e) {
                log.warn("seoul_academy row 파싱 실패: {}", e.getMessage());
            }
        }
        log.info("seoul_academy 파싱 완료: program {}건", programs.size());
        return new ParseResult(institutions, programs);
    }


    private String buildProgramTitle(String academyName, String courseName) {
        if (courseName == null || courseName.isBlank()) {
            return academyName;
        }
        return academyName + " - " + courseName;
    }

    private int parsePrice(String raw) {
        if (raw == null || raw.isBlank()) return 0;
        try { return Integer.parseInt(raw.replaceAll("[^0-9]", "")); }
        catch (Exception e) { return 0; }
    }

    private Integer parseIntOrNull(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return Integer.parseInt(raw.trim()); }
        catch (Exception e) { return null; }
    }
}
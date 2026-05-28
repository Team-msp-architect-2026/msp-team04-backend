package com.moment.momentbackend.batch.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.moment.momentbackend.program.entity.Institution;
import com.moment.momentbackend.program.entity.Program;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class SeoulProgramParser {

    private static final String EXTERNAL_SOURCE = "SEOUL_PUBLIC_PROGRAM";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    // sourceDetail → root key 매핑
    private static final Map<String, String> ROOT_KEY_MAP = Map.of(
            "education",   "ListPublicReservationEducation",
            "culture",     "ListPublicReservationCulture",
            "sport",       "ListPublicReservationSport",
            "medical",     "ListPublicReservationMedical",
            "institution", "ListPublicReservationInstitution"
    );

    public record ParseResult(List<Institution> institutions, List<Program> programs) {}

    public ParseResult parse(JsonNode root, String sourceDetail) {
        String rootKey = ROOT_KEY_MAP.getOrDefault(sourceDetail, "");
        JsonNode rowArray = root.path(rootKey).path("row");

        List<Institution> institutions = new ArrayList<>();
        List<Program> programs = new ArrayList<>();

        if (!rowArray.isArray()) {
            log.warn("seoul_public_program/{}: row 배열 없음", sourceDetail);
            return new ParseResult(institutions, programs);
        }

        for (JsonNode row : rowArray) {
            try {
                String svcId = row.path("SVCID").asText(null);
                if (svcId == null) continue;

                // Institution
                Institution institution = Institution.builder()
                        .institutionName(row.path("PLACENM").asText("미상"))
                        .institutionType("PUBLIC")
                        .address(row.path("AREANM").asText(null))
                        .phone(row.path("TELNO").asText(null))
                        .latitude(parseBigDecimal(row.path("Y").asText(null)))
                        .longitude(parseBigDecimal(row.path("X").asText(null)))
                        .externalSource(EXTERNAL_SOURCE)
                        .externalId("INST_" + svcId)
                        .lastSyncedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build();
                institutions.add(institution);

                // Program
                boolean isFree = "무료".equals(row.path("PAYATNM").asText(""));
                Program program = Program.builder()
                        .title(row.path("SVCNM").asText("제목없음"))
                        .category(mapCategory(sourceDetail))
                        .description(row.path("DTLCONT").asText(null))
                        .region(row.path("AREANM").asText(null))
                        .price(isFree ? 0 : 0)
                        .isFree(isFree)
                        .operationStart(parseDate(row.path("SVCOPNBGNDT").asText(null)))
                        .operationEnd(parseDate(row.path("SVCOPNENDDT").asText(null)))
                        .deadlineDate(parseDate(row.path("RCPTENDDT").asText(null)))
                        .imageUrl(row.path("IMGURL").asText(null))
                        .contactUrl(row.path("SVCURL").asText(null))
                        .contactPhone(row.path("TELNO").asText(null))
                        .latitude(parseBigDecimal(row.path("Y").asText(null)))
                        .longitude(parseBigDecimal(row.path("X").asText(null)))
                        .isRecruiting(true)
                        .isPublic(true)
                        .ratingAvg(BigDecimal.ZERO)
                        .reviewCount(0)
                        .externalSource(EXTERNAL_SOURCE)
                        .externalId(svcId)
                        .lastSyncedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build();
                programs.add(program);

            } catch (Exception e) {
                log.warn("seoul_public_program row 파싱 실패: {}", e.getMessage());
            }
        }
        log.info("seoul_public_program/{} 파싱 완료: program {}건", sourceDetail, programs.size());
        return new ParseResult(institutions, programs);
    }

    private String mapCategory(String sourceDetail) {
        return switch (sourceDetail) {
            case "education"   -> "EDUCATION";
            case "culture"     -> "ART";
            case "sport"       -> "SPORTS";
            case "medical"     -> "ETC";
            case "institution" -> "ETC";
            default            -> "ETC";
        };
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return LocalDate.parse(raw.substring(0, 10)); }
        catch (Exception e) { return null; }
    }

    private BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return new BigDecimal(raw); }
        catch (Exception e) { return null; }
    }
}
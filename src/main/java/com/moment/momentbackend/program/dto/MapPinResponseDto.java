package com.moment.momentbackend.program.dto;

import com.moment.momentbackend.program.entity.Program;
import lombok.Getter;

@Getter
public class MapPinResponseDto {

    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String category;
    private String status;      // RECRUITING / CLOSED
    private String pinColor;    // BLUE / GREEN / ORANGE / GRAY
    private Double distanceKm;  // 내 주변 찾기일 때만 값 존재, 그 외 null

    public MapPinResponseDto(Program program) {
        this(program, null);
    }

    public MapPinResponseDto(Program program, Double distanceKm) {
        this.id = program.getId();
        this.name = program.getTitle();
        this.latitude = program.getLatitude() != null
                ? program.getLatitude().doubleValue() : null;
        this.longitude = program.getLongitude() != null
                ? program.getLongitude().doubleValue() : null;
        this.category = program.getCategory();
        this.status = program.getIsRecruiting() ? "RECRUITING" : "CLOSED";
        this.pinColor = resolvePinColor(program);
        this.distanceKm = distanceKm;
    }

    private String resolvePinColor(Program program) {
        // 마감이면 무조건 회색
        if (!program.getIsRecruiting()) return "GRAY";

        // 모집 중일 때 타입 기준
        String type = program.getProgramType();
        if ("PUBLIC".equals(type) || "GOVERNMENT".equals(type)) return "BLUE";
        if ("PRIVATE".equals(type)) return "ORANGE";

        // 기본값: 모집중 초록
        return "GREEN";
    }
}
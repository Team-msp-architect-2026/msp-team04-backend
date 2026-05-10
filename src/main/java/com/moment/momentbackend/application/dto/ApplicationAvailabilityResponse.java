package com.moment.momentbackend.application.dto;

import com.moment.momentbackend.program.entity.Program;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationAvailabilityResponse {

    private Long programId;
    private String programTitle;
    private Boolean isRecruiting;
    private Integer maxCapacity;
    private Integer remainCapacity;
    private Boolean available;
    private String message;

    public static ApplicationAvailabilityResponse of(Program program) {
        boolean available = program.isAvailableForApplication();

        return ApplicationAvailabilityResponse.builder()
                .programId(program.getId())
                .programTitle(program.getTitle())
                .isRecruiting(program.getIsRecruiting())
                .maxCapacity(program.getMaxCapacity())
                .remainCapacity(program.getRemainCapacity())
                .available(available)
                .message(available ? "신청 가능한 프로그램입니다." : "현재 신청할 수 없는 프로그램입니다.")
                .build();
    }
}
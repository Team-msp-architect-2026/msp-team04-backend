package com.moment.momentbackend.application.dto;

import com.moment.momentbackend.application.entity.Application;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationCreateResponse {

    private Long applicationId;
    private Long programId;
    private String programTitle;
    private Integer reserveNo;
    private String applicationStatus;
    private LocalDateTime seatLockedUntil;
    private Integer remainCapacity;

    public static ApplicationCreateResponse of(
            Application application,
            String programTitle,
            Integer remainCapacity
    ) {
        return ApplicationCreateResponse.builder()
                .applicationId(application.getId())
                .programId(application.getProgramId())
                .programTitle(programTitle)
                .reserveNo(application.getReserveNo())
                .applicationStatus(application.getApplicationStatus().name())
                .seatLockedUntil(application.getSeatLockedUntil())
                .remainCapacity(remainCapacity)
                .build();
    }
}
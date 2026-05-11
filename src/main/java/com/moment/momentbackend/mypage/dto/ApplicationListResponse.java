package com.moment.momentbackend.mypage.dto;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.type.ApplicationStatus;
import com.moment.momentbackend.payment.type.PaymentStatus;
import com.moment.momentbackend.program.entity.Program;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationListResponse {

    private Long applicationId;
    private String programTitle;
    private String institutionName;
    private String region;
    private String imageUrl;
    private LocalDateTime appliedAt;
    private ApplicationStatus applicationStatus;
    private PaymentStatus paymentStatus;

    public static ApplicationListResponse of(
            Application application,
            Program program,
            PaymentStatus paymentStatus
    ) {
        return ApplicationListResponse.builder()
                .applicationId(application.getId())
                .programTitle(program.getTitle())
                .institutionName(program.getInstitution() != null
                        ? program.getInstitution().getInstitutionName() : null)
                .region(program.getRegion())
                .imageUrl(program.getImageUrl())
                .appliedAt(application.getAppliedAt())
                .applicationStatus(application.getApplicationStatus())
                .paymentStatus(paymentStatus)
                .build();
    }
}

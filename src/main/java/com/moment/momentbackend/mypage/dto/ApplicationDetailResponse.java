package com.moment.momentbackend.mypage.dto;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.type.ApplicationStatus;
import com.moment.momentbackend.payment.entity.Payment;
import com.moment.momentbackend.payment.type.PaymentMethod;
import com.moment.momentbackend.payment.type.PaymentStatus;
import com.moment.momentbackend.program.entity.Program;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationDetailResponse {

    private Long applicationId;
    private String programTitle;
    private String institutionName;
    private String region;
    private String imageUrl;
    private String applicantName;
    private String parentName;
    private String phone;
    private LocalDateTime appliedAt;
    private ApplicationStatus applicationStatus;

    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private Integer paymentAmount;
    private String orderId;
    private String paymentKey;
    private LocalDateTime approvedAt;

    public static ApplicationDetailResponse of(
            Application application,
            Program program,
            Payment payment
    ) {
        return ApplicationDetailResponse.builder()
                .applicationId(application.getId())
                .programTitle(program.getTitle())
                .institutionName(program.getInstitution() != null
                        ? program.getInstitution().getInstitutionName() : null)
                .region(program.getRegion())
                .imageUrl(program.getImageUrl())
                .applicantName(application.getApplicantName())
                .parentName(application.getParentName())
                .phone(application.getPhone())
                .appliedAt(application.getAppliedAt())
                .applicationStatus(application.getApplicationStatus())
                .paymentStatus(payment != null ? payment.getPaymentStatus() : null)
                .paymentMethod(payment != null ? payment.getPaymentMethod() : null)
                .paymentAmount(payment != null ? payment.getPaymentAmount() : null)
                .orderId(payment != null ? payment.getOrderId() : null)
                .paymentKey(payment != null ? payment.getPaymentKey() : null)
                .approvedAt(payment != null ? payment.getApprovedAt() : null)
                .build();
    }
}

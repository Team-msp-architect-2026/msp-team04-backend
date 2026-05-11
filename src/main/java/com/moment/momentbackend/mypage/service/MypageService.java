package com.moment.momentbackend.mypage.service;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.repository.ApplicationRepository;
import com.moment.momentbackend.application.type.ApplicationStatus;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.mypage.dto.ApplicationDetailResponse;
import com.moment.momentbackend.mypage.dto.ApplicationListResponse;
import com.moment.momentbackend.payment.entity.Payment;
import com.moment.momentbackend.payment.repository.PaymentRepository;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final ApplicationRepository applicationRepository;
    private final PaymentRepository paymentRepository;
    private final ProgramRepository programRepository;

    @Transactional(readOnly = true)
    public List<ApplicationListResponse> getApplicationList(Long userId, ApplicationStatus status) {
        List<Application> applications = (status == null)
                ? applicationRepository.findByUserIdOrderByAppliedAtDesc(userId)
                : applicationRepository.findByUserIdAndApplicationStatusOrderByAppliedAtDesc(userId, status);

        return applications.stream()
                .map(application -> {
                    Program program = programRepository.findById(application.getProgramId())
                            .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));
                    Payment payment = paymentRepository.findByApplicationId(application.getId())
                            .orElse(null);
                    return ApplicationListResponse.of(
                            application,
                            program,
                            payment != null ? payment.getPaymentStatus() : null
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationDetailResponse getApplicationDetail(Long userId, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Program program = programRepository.findById(application.getProgramId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        Payment payment = paymentRepository.findByApplicationId(applicationId)
                .orElse(null);

        return ApplicationDetailResponse.of(application, program, payment);
    }
}

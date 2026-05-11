package com.moment.momentbackend.application.scheduler;

import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.repository.ApplicationRepository;
import com.moment.momentbackend.application.type.ApplicationStatus;
import com.moment.momentbackend.payment.entity.Payment;
import com.moment.momentbackend.payment.repository.PaymentRepository;
import com.moment.momentbackend.payment.type.PaymentStatus;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatLockScheduler {

    private final ApplicationRepository applicationRepository;
    private final PaymentRepository paymentRepository;
    private final ProgramRepository programRepository;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void recoverExpiredSeatLocks() {
        LocalDateTime now = LocalDateTime.now();

        List<Application> expiredApplications = applicationRepository.findExpiredApplications(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.PAYMENT_READY),
                now
        );

        if (expiredApplications.isEmpty()) {
            return;
        }

        log.info("[SeatLockScheduler] 만료 대상 신청 건수: {}", expiredApplications.size());

        int restoredCount = 0;

        for (Application application : expiredApplications) {
            try {
                Optional<Payment> paymentOpt = paymentRepository.findByApplicationId(application.getId());
                if (paymentOpt.isPresent() && paymentOpt.get().getPaymentStatus() == PaymentStatus.APPROVED) {
                    log.warn("[SeatLockScheduler] applicationId={} 결제 승인 완료 건 - 스킵", application.getId());
                    continue;
                }

                application.changeStatus(ApplicationStatus.FAILED);

                paymentOpt.ifPresent(payment -> {
                    if (payment.getPaymentStatus() == PaymentStatus.READY) {
                        payment.expire("EXPIRED", "좌석 락 만료로 인한 결제 자동 만료");
                    }
                });

                Program program = programRepository.findById(application.getProgramId())
                        .orElse(null);

                if (program != null) {
                    program.restoreRemainCapacity();
                    restoredCount++;
                    log.info("[SeatLockScheduler] 좌석 복구 완료 - applicationId={}, programId={}, remainCapacity={}",
                            application.getId(), program.getId(), program.getRemainCapacity());
                }

            } catch (Exception e) {
                log.error("[SeatLockScheduler] 복구 실패 - applicationId={}, error={}",
                        application.getId(), e.getMessage());
            }
        }

        log.info("[SeatLockScheduler] 복구 완료 - 총 복구 좌석 수: {}", restoredCount);
    }
}

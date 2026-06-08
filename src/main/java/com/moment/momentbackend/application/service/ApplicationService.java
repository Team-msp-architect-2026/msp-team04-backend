package com.moment.momentbackend.application.service;

import com.moment.momentbackend.application.dto.ApplicationAvailabilityResponse;
import com.moment.momentbackend.application.dto.ApplicationCreateRequest;
import com.moment.momentbackend.application.dto.ApplicationCreateResponse;
import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.repository.ApplicationRepository;
import com.moment.momentbackend.application.type.ApplicationStatus;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.global.metrics.BusinessMetricsService;
import com.moment.momentbackend.global.redis.RedisService;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private static final long APPLICATION_LOCK_TTL_SECONDS = 5L;
    private static final int SEAT_LOCK_MINUTES = 10;

    private final ApplicationRepository applicationRepository;
    private final ProgramRepository programRepository;
    private final ChildProfileRepository childProfileRepository;
    private final RedisService redisService;
    private final BusinessMetricsService businessMetricsService;

    @Transactional(readOnly = true)
    public ApplicationAvailabilityResponse getAvailability(Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        return ApplicationAvailabilityResponse.of(program);
    }

    @Transactional
    public ApplicationCreateResponse createApplication(Long userId, ApplicationCreateRequest request) {
        try {
            return createApplicationInternal(userId, request);
        } catch (CustomException e) {
            businessMetricsService.recordApplicationFailed(e.getErrorCode().name());
            throw e;
        } catch (RuntimeException e) {
            businessMetricsService.recordApplicationFailed("system_error");
            throw e;
        }
    }

    private ApplicationCreateResponse createApplicationInternal(Long userId, ApplicationCreateRequest request) {
        validateAuthenticatedUser(userId);
        validateAgreement(request);

        String lockKey = buildApplicationLockKey(request.getProgramId());
        String lockValue = UUID.randomUUID().toString();

        boolean locked = redisService.tryLock(lockKey, lockValue, APPLICATION_LOCK_TTL_SECONDS);
        if (!locked) {
            throw new CustomException(ErrorCode.APPLICATION_LOCK_FAILED);
        }

        try {
            validateChildOwner(request.getChildId(), userId);

            Program program = programRepository.findByIdForUpdate(request.getProgramId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

            validateDuplicateApplication(userId, request.getChildId(), request.getProgramId());
            validateProgramAvailable(program);

            program.decreaseRemainCapacity();

            Application application = Application.createPending(
                    userId,
                    request.getChildId(),
                    program.getId(),
                    request.getApplicantName(),
                    request.getParentName(),
                    request.getPhone(),
                    request.getRequestNote(),
                    request.getAiStartMessage(),
                    request.getAgreeTerms(),
                    request.getAgreePrivacy(),
                    createReserveNo(),
                    LocalDateTime.now().plusMinutes(SEAT_LOCK_MINUTES)
            );

            Application savedApplication = applicationRepository.save(application);

            ApplicationCreateResponse response = ApplicationCreateResponse.of(
                    savedApplication,
                    program.getTitle(),
                    program.getRemainCapacity()
            );

            businessMetricsService.recordApplicationCreated();

            return response;
        } finally {
            redisService.unlock(lockKey, lockValue);
        }
    }

    private void validateAuthenticatedUser(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void validateAgreement(ApplicationCreateRequest request) {
        if (!Boolean.TRUE.equals(request.getAgreeTerms())
                || !Boolean.TRUE.equals(request.getAgreePrivacy())) {
            throw new CustomException(ErrorCode.INVALID_AGREEMENT);
        }
    }

    private void validateChildOwner(Long childId, Long userId) {
        childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));
    }

    private void validateDuplicateApplication(Long userId, Long childId, Long programId) {
        boolean exists = applicationRepository.existsByUserIdAndChildIdAndProgramIdAndApplicationStatusIn(
                userId,
                childId,
                programId,
                List.of(
                        ApplicationStatus.PENDING,
                        ApplicationStatus.PAYMENT_READY,
                        ApplicationStatus.CONFIRMED
                )
        );

        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_APPLIED);
        }
    }

    private void validateProgramAvailable(Program program) {
        if (!Boolean.TRUE.equals(program.getIsPublic())) {
            throw new CustomException(ErrorCode.PROGRAM_NOT_FOUND);
        }

        if (!Boolean.TRUE.equals(program.getIsRecruiting())) {
            throw new CustomException(ErrorCode.APPLICATION_NOT_AVAILABLE);
        }

        if (program.getRemainCapacity() == null || program.getRemainCapacity() <= 0) {
            throw new CustomException(ErrorCode.SOLD_OUT);
        }
    }

    private String buildApplicationLockKey(Long programId) {
        return "application:program:" + programId;
    }

    private Integer createReserveNo() {
        long value = System.currentTimeMillis() % 1_000_000_000L;
        return (int) value;
    }
}
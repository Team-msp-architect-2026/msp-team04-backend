package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.dto.StartDateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class StartDateService {

    private final ProgramRepository programRepository;
    private final ChildProfileRepository childProfileRepository;

    @Transactional(readOnly = true)
    public StartDateResponseDto getStartDate(Long userId, Long programId, Long profileId) {

        var child = childProfileRepository.findByIdAndUserId(profileId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        // 나이 계산 및 적합성 확인
        int childAge = Period.between(child.getBirthDate(), LocalDate.now()).getYears();
        boolean isAgeEligible = true;
        if (program.getTargetAgeMin() != null && childAge < program.getTargetAgeMin()) isAgeEligible = false;
        if (program.getTargetAgeMax() != null && childAge > program.getTargetAgeMax()) isAgeEligible = false;

        // 최적 시작일 계산
        LocalDate today = LocalDate.now();
        LocalDate optimalStartDate;
        if (program.getOperationStart() != null && program.getOperationStart().isAfter(today)) {
            optimalStartDate = program.getOperationStart();
        } else {
            optimalStartDate = today;
        }

        String message = String.format("최적의 시작일은 %d월 %d일이에요",
                optimalStartDate.getMonthValue(), optimalStartDate.getDayOfMonth());

        return StartDateResponseDto.builder()
                .programId(program.getId())
                .programTitle(program.getTitle())
                .operationStart(program.getOperationStart())
                .operationEnd(program.getOperationEnd())
                .classTime(program.getClassTime())
                .classType(program.getClassType())
                .targetAgeMin(program.getTargetAgeMin())
                .targetAgeMax(program.getTargetAgeMax())
                .childAge(childAge)
                .isAgeEligible(isAgeEligible)
                .optimalStartDate(optimalStartDate)
                .message(message)
                .build();
    }
}
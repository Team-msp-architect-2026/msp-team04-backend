package com.moment.momentbackend.program.service;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.dto.HomeProgramsResponseDto;
import com.moment.momentbackend.program.dto.MapPinResponseDto;
import com.moment.momentbackend.program.dto.ProgramDetailResponseDto;
import com.moment.momentbackend.program.dto.ProgramListResponseDto;
import com.moment.momentbackend.program.repository.MapPinQueryRepository;
import com.moment.momentbackend.program.repository.ProgramListQueryRepository;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramListQueryRepository programListQueryRepository;
    private final ProgramRepository programRepository;
    private final MapPinQueryRepository mapPinQueryRepository;

    @Transactional(readOnly = true)
    public Page<ProgramListResponseDto> getPrograms(String status, String category,
                                                    String region, Pageable pageable) {
        return programListQueryRepository
                .findPrograms(status, category, region, pageable)
                .map(ProgramListResponseDto::new);
    }

    @Transactional(readOnly = true)
    public ProgramDetailResponseDto getProgram(Long id) {
        return new ProgramDetailResponseDto(
                programRepository.findDetailById(id)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND))
        );
    }

    @Cacheable(value = "homePrograms", key = "#region + ':' + #category")
    @Transactional(readOnly = true)
    public HomeProgramsResponseDto getHomePrograms(String region, String category) {
        int page = 0;
        int size = 500;
        int targetCount = 300;

        List<ProgramListResponseDto> futurePrograms = new ArrayList<>();
        Page<ProgramListResponseDto> result;

        do {
            PageRequest pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.ASC, "deadlineDate")
            );

            result = programListQueryRepository
                    .findPrograms(null, category, region, pageable)
                    .map(ProgramListResponseDto::new);

            List<ProgramListResponseDto> validPrograms = result.getContent().stream()
                    .filter(this::hasValidFutureDeadline)
                    .toList();

            futurePrograms.addAll(validPrograms);
            page++;

        } while (result.hasNext() && futurePrograms.size() < targetCount);

        List<ProgramListResponseDto> recruitingPrograms = futurePrograms.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsRecruiting()))
                .toList();

        List<ProgramListResponseDto> freePrograms = recruitingPrograms.stream()
                .filter(this::isFreeProgram)
                .limit(5)
                .toList();

        List<ProgramListResponseDto> urgentPrograms = recruitingPrograms.stream()
                .filter(this::isUrgentProgram)
                .sorted(Comparator.comparing(ProgramListResponseDto::getDeadlineDate))
                .limit(5)
                .toList();

        List<ProgramListResponseDto> onlinePrograms = recruitingPrograms.stream()
                .filter(this::isOnlineProgram)
                .limit(5)
                .toList();

        return new HomeProgramsResponseDto(
                freePrograms,
                urgentPrograms,
                onlinePrograms
        );
    }

    private boolean hasValidFutureDeadline(ProgramListResponseDto program) {
        LocalDate deadline = parseDeadline(program.getDeadlineDate());

        if (deadline == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        return !deadline.isBefore(today);
    }

    private boolean isFreeProgram(ProgramListResponseDto program) {
        return Boolean.TRUE.equals(program.getIsFree())
                || (program.getPrice() != null && program.getPrice() == 0);
    }

    private boolean isUrgentProgram(ProgramListResponseDto program) {
        LocalDate deadline = parseDeadline(program.getDeadlineDate());

        if (deadline == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        long daysLeft = ChronoUnit.DAYS.between(today, deadline);

        // 오늘부터 14일 이내 마감
        return daysLeft >= 0 && daysLeft <= 14;
    }

    private boolean isOnlineProgram(ProgramListResponseDto program) {
        String classType = program.getClassType();
        String detailAddress = program.getDetailAddress();
        String description = program.getDescription();
        String name = program.getName();

        return containsOnline(classType)
                || containsOnline(detailAddress)
                || containsOnline(description)
                || containsOnline(name);
    }

    private boolean containsOnline(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String lowerValue = value.toLowerCase();

        return value.contains("온라인")
                || value.contains("비대면")
                || lowerValue.contains("online");
    }

    private LocalDate parseDeadline(String deadlineDate) {
        if (deadlineDate == null || deadlineDate.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(deadlineDate);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Page<ProgramListResponseDto> getPrograms(String status, String category,
                                                    String region, String filter,
                                                    Pageable pageable) {
        return programListQueryRepository
                .findPrograms(status, category, region, filter, pageable)
                .map(ProgramListResponseDto::new);
    }

    @Transactional(readOnly = true)
    public List<MapPinResponseDto> getMapPins(String region) {
        return mapPinQueryRepository.findMapPins(region)
                .stream()
                .map(MapPinResponseDto::new)
                .collect(Collectors.toList());
    }
}
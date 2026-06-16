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
import com.moment.momentbackend.program.entity.Program;

import java.math.BigDecimal;
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

    @Cacheable(value = "homeProgramsV2", key = "#region + ':' + #category")
    @Transactional(readOnly = true)
    public HomeProgramsResponseDto getHomePrograms(String region, String category) {
        PageRequest defaultPageable = PageRequest.of(
                0,
                5,
                Sort.by(Sort.Direction.ASC, "deadlineDate")
        );

        PageRequest urgentPageable = PageRequest.of(
                0,
                5,
                Sort.by(Sort.Direction.ASC, "deadlineDate")
        );

        List<ProgramListResponseDto> freePrograms = programListQueryRepository
                .findPrograms("RECRUITING", category, region, "FREE", defaultPageable)
                .map(ProgramListResponseDto::new)
                .getContent();

        List<ProgramListResponseDto> urgentPrograms = programListQueryRepository
                .findPrograms("RECRUITING", category, region, "URGENT", urgentPageable)
                .map(ProgramListResponseDto::new)
                .getContent();

        List<ProgramListResponseDto> onlinePrograms = programListQueryRepository
                .findPrograms("RECRUITING", category, region, "ONLINE", defaultPageable)
                .map(ProgramListResponseDto::new)
                .getContent();

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

    /**
     * 내 주변 찾기: lat/lng 기준 반경(km) 내 프로그램 핀 조회
     */
    @Transactional(readOnly = true)
    public List<MapPinResponseDto> getNearbyMapPins(double lat, double lng, double radiusKm, Integer limit) {
        return mapPinQueryRepository.findNearbyPins(lat, lng, radiusKm, limit)
                .stream()
                .map(program -> new MapPinResponseDto(program, calculateDistanceKm(lat, lng, program)))
                .collect(Collectors.toList());
    }

    private Double calculateDistanceKm(double lat, double lng, Program program) {
        if (program.getLatitude() == null || program.getLongitude() == null) return null;

        double pLat = program.getLatitude().doubleValue();
        double pLng = program.getLongitude().doubleValue();

        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(pLat - lat);
        double dLng = Math.toRadians(pLng - lng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(pLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(earthRadiusKm * c * 100) / 100.0;
    }
}
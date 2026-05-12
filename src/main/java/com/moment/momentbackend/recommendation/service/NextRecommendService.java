package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.application.repository.ApplicationRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.dto.NextRecommendResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NextRecommendService {

    private final ApplicationRepository applicationRepository;
    private final ProgramRepository programRepository;

    private static final Map<String, List<String>> COMPLEMENT_MAP = new HashMap<>();
    static {
        COMPLEMENT_MAP.put("미술", List.of("음악", "체육", "창의"));
        COMPLEMENT_MAP.put("음악", List.of("미술", "체육", "댄스"));
        COMPLEMENT_MAP.put("체육", List.of("미술", "음악"));
        COMPLEMENT_MAP.put("수학", List.of("과학", "코딩", "영어"));
        COMPLEMENT_MAP.put("영어", List.of("수학", "독서", "미술"));
        COMPLEMENT_MAP.put("코딩", List.of("수학", "과학"));
        COMPLEMENT_MAP.put("독서", List.of("영어", "미술"));
        COMPLEMENT_MAP.put("과학", List.of("수학", "코딩"));
    }

    @Transactional(readOnly = true)
    public NextRecommendResponseDto getNextRecommend(Long userId, Long reservationId) {

        var application = applicationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Program applied = programRepository.findById(application.getProgramId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        // 보완 카테고리 조회
        List<String> complementCategories = COMPLEMENT_MAP.getOrDefault(
                applied.getCategory(), List.of());

        List<Program> nextPrograms;
        if (!complementCategories.isEmpty()) {
            nextPrograms = programRepository
                    .findComplementaryPrograms(complementCategories)
                    .stream().limit(3).toList();
        } else {
            nextPrograms = programRepository
                    .findOtherRecruitingPrograms(applied.getCategory())
                    .stream().limit(3).toList();
        }

        List<NextRecommendResponseDto.NextProgramDto> recommendations = nextPrograms.stream()
                .map(p -> NextRecommendResponseDto.NextProgramDto.builder()
                        .programId(p.getId())
                        .title(p.getTitle())
                        .category(p.getCategory())
                        .classTime(p.getClassTime())
                        .ratingAvg(p.getRatingAvg())
                        .imageUrl(p.getImageUrl())
                        .reason(String.format("%s 수업과 함께하면 균형 잡힌 성장에 도움이 돼요", applied.getCategory()))
                        .build())
                .toList();

        return NextRecommendResponseDto.builder()
                .appliedProgramId(applied.getId())
                .appliedProgramTitle(applied.getTitle())
                .appliedProgramCategory(applied.getCategory())
                .nextRecommendations(recommendations)
                .build();
    }
}
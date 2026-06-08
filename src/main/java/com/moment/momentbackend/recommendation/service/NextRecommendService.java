package com.moment.momentbackend.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.momentbackend.application.entity.Application;
import com.moment.momentbackend.application.repository.ApplicationRepository;
import com.moment.momentbackend.application.type.ApplicationStatus;
import com.moment.momentbackend.child.entity.ChildConcern;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildConcernRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.global.metrics.BusinessMetricsService;
import com.moment.momentbackend.global.redis.RedisService;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.client.NextRecommendAiClient;
import com.moment.momentbackend.recommendation.dto.NextRecommendAiRequest;
import com.moment.momentbackend.recommendation.dto.NextRecommendAppliedProgramRequest;
import com.moment.momentbackend.recommendation.dto.NextRecommendCandidateRequest;
import com.moment.momentbackend.recommendation.dto.NextRecommendChildRequest;
import com.moment.momentbackend.recommendation.dto.NextRecommendExplainItemResponse;
import com.moment.momentbackend.recommendation.dto.NextRecommendExplainResponse;
import com.moment.momentbackend.recommendation.dto.NextRecommendResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NextRecommendService {

    private static final long CACHE_TTL_SECONDS = 60 * 60 * 24;

    private final ApplicationRepository applicationRepository;
    private final ProgramRepository programRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ChildConcernRepository childConcernRepository;
    private final NextRecommendAiClient nextRecommendAiClient;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final BusinessMetricsService businessMetricsService;

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

        COMPLEMENT_MAP.put("ART", List.of("SPORTS", "EXPERIENCE", "CARE"));
        COMPLEMENT_MAP.put("SPORTS", List.of("ART", "CARE", "EXPERIENCE"));
        COMPLEMENT_MAP.put("EDUCATION", List.of("EXPERIENCE", "ART", "CARE"));
        COMPLEMENT_MAP.put("LANGUAGE", List.of("EDUCATION", "ART", "CARE"));
        COMPLEMENT_MAP.put("EXPERIENCE", List.of("EDUCATION", "ART", "SPORTS"));
        COMPLEMENT_MAP.put("CARE", List.of("EDUCATION", "ART", "EXPERIENCE"));
        COMPLEMENT_MAP.put("ONLINE", List.of("EDUCATION", "LANGUAGE", "EXPERIENCE"));
    }

    @Transactional(readOnly = true)
    public NextRecommendResponseDto getNextRecommend(Long userId, Long reservationId) {
        return businessMetricsService.recordRecommendation(
                "next",
                () -> getNextRecommendInternal(userId, reservationId)
        );
    }

    private NextRecommendResponseDto getNextRecommendInternal(Long userId, Long reservationId) {
        validateUserId(userId);

        Application application = applicationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        Program applied = programRepository.findById(application.getProgramId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        List<Program> nextPrograms = selectNextPrograms(applied);

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

    @Transactional(readOnly = true)
    public NextRecommendExplainResponse explainNextRecommend(Long userId, Long applicationId) {
        return businessMetricsService.recordRecommendation(
                "next_explain",
                () -> explainNextRecommendInternal(userId, applicationId)
        );
    }

    private NextRecommendExplainResponse explainNextRecommendInternal(Long userId, Long applicationId) {
        validateUserId(userId);

        Application application = applicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        validateConfirmedApplication(application);

        Program applied = programRepository.findById(application.getProgramId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        ChildProfile child = childProfileRepository.findByIdAndUserId(application.getChildId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        List<String> concerns = childConcernRepository.findByChildProfileId(child.getId()).stream()
                .map(ChildConcern::getConcern)
                .filter(Objects::nonNull)
                .filter(concern -> !concern.isBlank())
                .toList();

        List<Program> candidates = selectNextPrograms(applied);

        if (candidates.isEmpty()) {
            return buildEmptyFallbackResponse(applied);
        }

        NextRecommendAiRequest aiRequest = buildAiRequest(child, concerns, applied, candidates);
        String cacheKey = buildCacheKey(applicationId, aiRequest);

        NextRecommendExplainResponse response = redisService.getValue(cacheKey)
                .map(this::readCachedResponse)
                .filter(this::isValidAiResponse)
                .orElseGet(() -> generateAndCache(cacheKey, aiRequest));

        String source = response.source();
        businessMetricsService.recordRecommendationSource("next_explain", source);

        return response;
    }

    private NextRecommendExplainResponse generateAndCache(
            String cacheKey,
            NextRecommendAiRequest aiRequest
    ) {
        NextRecommendExplainResponse response = nextRecommendAiClient.generate(aiRequest)
                .filter(this::isValidAiResponse)
                .orElseGet(() -> buildFallbackResponse(aiRequest));

        if (!"FALLBACK".equals(response.source())) {
            redisService.setValue(
                    cacheKey,
                    writeResponse(response),
                    CACHE_TTL_SECONDS
            );
        }

        return response;
    }

    private NextRecommendAiRequest buildAiRequest(
            ChildProfile child,
            List<String> concerns,
            Program applied,
            List<Program> candidates
    ) {
        return new NextRecommendAiRequest(
                new NextRecommendChildRequest(
                        child.getId(),
                        calculateAge(child),
                        concerns
                ),
                new NextRecommendAppliedProgramRequest(
                        applied.getId(),
                        trim(applied.getTitle()),
                        trim(applied.getCategory()),
                        trim(applied.getDescription()),
                        trim(applied.getClassTime()),
                        applied.getPrice(),
                        applied.getIsFree(),
                        toDouble(applied.getRatingAvg())
                ),
                candidates.stream()
                        .map(candidate -> new NextRecommendCandidateRequest(
                                candidate.getId(),
                                trim(candidate.getTitle()),
                                trim(candidate.getCategory()),
                                trim(candidate.getDescription()),
                                trim(candidate.getClassTime()),
                                candidate.getPrice(),
                                candidate.getIsFree(),
                                toDouble(candidate.getRatingAvg()),
                                buildReasonBasis(applied, candidate)
                        ))
                        .toList()
        );
    }

    private List<Program> selectNextPrograms(Program applied) {
        List<String> complementCategories = COMPLEMENT_MAP.getOrDefault(
                trim(applied.getCategory()),
                List.of()
        );

        List<Program> nextPrograms;

        if (!complementCategories.isEmpty()) {
            nextPrograms = programRepository
                    .findComplementaryPrograms(complementCategories);
        } else {
            nextPrograms = programRepository
                    .findOtherRecruitingPrograms(applied.getCategory());
        }

        return nextPrograms.stream()
                .filter(program -> !program.getId().equals(applied.getId()))
                .sorted(Comparator.comparing(Program::getRatingAvg, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .toList();
    }

    private String buildReasonBasis(Program applied, Program candidate) {
        String appliedCategory = trim(applied.getCategory());
        String candidateCategory = trim(candidate.getCategory());

        if (!appliedCategory.isBlank() && !candidateCategory.isBlank()) {
            return appliedCategory + " 신청 후 " + candidateCategory + " 활동을 함께 비교하는 보완 추천";
        }

        return "신청 완료 프로그램과 함께 비교할 다음 추천 후보";
    }

    private NextRecommendExplainResponse buildFallbackResponse(NextRecommendAiRequest request) {
        List<NextRecommendExplainItemResponse> items = new ArrayList<>();

        for (NextRecommendCandidateRequest candidate : request.candidates()) {
            items.add(new NextRecommendExplainItemResponse(
                    candidate.programId(),
                    candidate.title(),
                    buildFallbackCandidateReason(request.appliedProgram(), candidate),
                    buildFallbackTag(candidate)
            ));
        }

        return new NextRecommendExplainResponse(
                buildFallbackMessage(request),
                items,
                "FALLBACK"
        );
    }

    private NextRecommendExplainResponse buildEmptyFallbackResponse(Program applied) {
        return new NextRecommendExplainResponse(
                trim(applied.getTitle()) + " 신청이 완료됐어요. 새로운 추천 후보가 생기면 다시 알려드릴게요.",
                List.of(),
                "FALLBACK"
        );
    }

    private String buildFallbackMessage(NextRecommendAiRequest request) {
        String title = request.appliedProgram() != null
                ? trim(request.appliedProgram().title())
                : "신청한 프로그램";

        return title + " 신청 후 함께 비교해볼 만한 다음 추천 프로그램을 정리했어요.";
    }

    private String buildFallbackCandidateReason(
            NextRecommendAppliedProgramRequest applied,
            NextRecommendCandidateRequest candidate
    ) {
        String appliedCategory = applied != null ? trim(applied.category()) : "";

        if (!appliedCategory.isBlank()) {
            return appliedCategory + " 수업과 함께 " + trim(candidate.title()) + "도 비교해보면 좋아요.";
        }

        return trim(candidate.title()) + "도 아이 조건과 함께 비교해볼 만한 프로그램이에요.";
    }

    private String buildFallbackTag(NextRecommendCandidateRequest candidate) {
        String category = trim(candidate.category());

        if (!category.isBlank()) {
            return category.length() > 12 ? category.substring(0, 12) : category;
        }

        return "다음 추천";
    }

    private boolean isValidAiResponse(NextRecommendExplainResponse response) {
        return response != null
                && response.message() != null
                && !response.message().isBlank()
                && response.items() != null;
    }

    private String buildCacheKey(Long applicationId, NextRecommendAiRequest request) {
        return "ai:next-recommend:" + applicationId + ":" + sha256(writeRequest(request));
    }

    private String writeRequest(NextRecommendAiRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private String writeResponse(NextRecommendExplainResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private NextRecommendExplainResponse readCachedResponse(String cachedValue) {
        try {
            return objectMapper.readValue(cachedValue, NextRecommendExplainResponse.class);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private void validateConfirmedApplication(Application application) {
        if (application.getApplicationStatus() != ApplicationStatus.CONFIRMED) {
            throw new CustomException(ErrorCode.INVALID_APPLICATION_STATUS);
        }
    }

    private Integer calculateAge(ChildProfile child) {
        if (child.getBirthDate() == null) {
            return null;
        }

        return Period.between(child.getBirthDate(), LocalDate.now()).getYears();
    }

    private Double toDouble(java.math.BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }

    private String trim(String value) {
        return value != null ? value.trim().replaceAll("\s+", " ") : "";
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}

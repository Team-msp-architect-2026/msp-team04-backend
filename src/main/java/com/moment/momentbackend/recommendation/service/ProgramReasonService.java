package com.moment.momentbackend.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.moment.momentbackend.recommendation.client.ProgramReasonAiClient;
import com.moment.momentbackend.recommendation.dto.ProgramReasonAiRequest;
import com.moment.momentbackend.recommendation.dto.ProgramReasonChildRequest;
import com.moment.momentbackend.recommendation.dto.ProgramReasonPreferenceRequest;
import com.moment.momentbackend.recommendation.dto.ProgramReasonProgramRequest;
import com.moment.momentbackend.recommendation.dto.ProgramReasonResponse;
import com.moment.momentbackend.recommendation.dto.ProgramReasonScoreRequest;
import com.moment.momentbackend.recommendation.dto.ScoreBreakdownDto;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.ReasonCode;
import com.moment.momentbackend.recommendation.repository.RecommendationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProgramReasonService {

    private static final long CACHE_TTL_SECONDS = 60 * 60 * 24;

    private final ProgramRepository programRepository;
    private final RecommendationPreferenceRepository preferenceRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ChildConcernRepository childConcernRepository;
    private final ScoringService scoringService;
    private final ProgramReasonAiClient programReasonAiClient;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final BusinessMetricsService businessMetricsService;

    @Transactional(readOnly = true)
    public ProgramReasonResponse generate(Long userId, Long programId, Long preferenceId, Long childId) {
        return businessMetricsService.recordRecommendation(
                "program_reason",
                () -> generateInternal(userId, programId, preferenceId, childId)
        );
    }

    private ProgramReasonResponse generateInternal(Long userId, Long programId, Long preferenceId, Long childId) {
        validateUserId(userId);

        RecommendationPreference preference = resolvePreference(userId, preferenceId, childId);

        ChildProfile child = childProfileRepository.findByIdAndUserId(preference.getChildId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        Program program = programRepository.findDetailById(programId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        List<String> concerns = childConcernRepository.findByChildProfileId(child.getId()).stream()
                .map(ChildConcern::getConcern)
                .filter(Objects::nonNull)
                .filter(concern -> !concern.isBlank())
                .toList();

        int childAge = calculateAge(child);

        ScoreBreakdownDto score = scoringService.calculate(
                program,
                preference,
                childAge,
                0.0,
                0.0,
                concerns
        );

        ProgramReasonAiRequest aiRequest = buildAiRequest(child, preference, program, concerns, childAge, score);
        String cacheKey = buildCacheKey(programId, preferenceId, aiRequest);

        ProgramReasonResponse response = redisService.getValue(cacheKey)
                .map(this::readCachedResponse)
                .orElseGet(() -> generateAndCache(cacheKey, aiRequest, score));

        String source = response.source();
        businessMetricsService.recordRecommendationSource("program_reason", source);

        return response;
    }

    private ProgramReasonResponse generateAndCache(
            String cacheKey,
            ProgramReasonAiRequest aiRequest,
            ScoreBreakdownDto score
    ) {
        ProgramReasonResponse response = programReasonAiClient.generate(aiRequest)
                .filter(this::isValidAiResponse)
                .orElseGet(() -> buildFallbackResponse(aiRequest, score));

        if (!"FALLBACK".equals(response.source())) {
            redisService.setValue(
                    cacheKey,
                    writeResponse(response),
                    CACHE_TTL_SECONDS
            );
        }

        return response;
    }

    private RecommendationPreference resolvePreference(Long userId, Long preferenceId, Long childId) {
        if (preferenceId != null) {
            RecommendationPreference preference = preferenceRepository.findByIdAndUserId(preferenceId, userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PREFERENCE_NOT_FOUND));

            if (childId != null && !childId.equals(preference.getChildId())) {
                throw new CustomException(ErrorCode.CHILD_ACCESS_DENIED);
            }

            return preference;
        }

        if (childId == null) {
            throw new CustomException(ErrorCode.PREFERENCE_NOT_FOUND);
        }

        childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        return preferenceRepository.findTopByUserIdAndChildIdOrderByCreatedAtDesc(userId, childId)
                .orElseThrow(() -> new CustomException(ErrorCode.PREFERENCE_NOT_FOUND));
    }

    private ProgramReasonAiRequest buildAiRequest(
            ChildProfile child,
            RecommendationPreference preference,
            Program program,
            List<String> concerns,
            int childAge,
            ScoreBreakdownDto score
    ) {
        return new ProgramReasonAiRequest(
                new ProgramReasonChildRequest(
                        child.getId(),
                        childAge,
                        concerns
                ),
                new ProgramReasonPreferenceRequest(
                        preference.getId(),
                        trim(preference.getRegion()),
                        enumName(preference.getMonthlyBudget()),
                        enumName(preference.getTransportType()),
                        enumName(preference.getMoveTime()),
                        enumName(preference.getOnlinePreference()),
                        enumName(preference.getClassType())
                ),
                new ProgramReasonProgramRequest(
                        program.getId(),
                        trim(program.getTitle()),
                        trim(program.getCategory()),
                        trim(program.getDescription()),
                        program.getInstitution() != null ? trim(program.getInstitution().getInstitutionName()) : null,
                        trim(program.getRegion()),
                        program.getPrice(),
                        program.getIsFree(),
                        trim(program.getClassType()),
                        program.getTargetAgeMin(),
                        program.getTargetAgeMax(),
                        program.getRatingAvg() != null ? program.getRatingAvg().doubleValue() : null,
                        program.getReviewCount(),
                        program.getTags() != null
                                ? program.getTags().stream()
                                .map(tag -> trim(tag.getTag()))
                                .filter(tag -> !tag.isBlank())
                                .toList()
                                : List.of()
                ),
                new ProgramReasonScoreRequest(
                        score.getTotalScore() != null ? score.getTotalScore().doubleValue() : 0.0,
                        score.getReasonCodes() != null
                                ? score.getReasonCodes().stream().map(ReasonCode::name).toList()
                                : List.of(),
                        buildScoreBreakdown(score)
                )
        );
    }

    private Map<String, Double> buildScoreBreakdown(ScoreBreakdownDto score) {
        Map<String, Double> scoreBreakdown = new LinkedHashMap<>();

        scoreBreakdown.put("scoreDistance", toDouble(score.getScoreDistance()));
        scoreBreakdown.put("scoreBudget", toDouble(score.getScoreBudget()));
        scoreBreakdown.put("scoreAge", toDouble(score.getScoreAge()));
        scoreBreakdown.put("scoreKeyword", toDouble(score.getScoreKeyword()));
        scoreBreakdown.put("scoreClassType", toDouble(score.getScoreClassType()));
        scoreBreakdown.put("scoreRecruiting", toDouble(score.getScoreRecruiting()));
        scoreBreakdown.put("scoreReview", toDouble(score.getScoreReview()));

        return scoreBreakdown;
    }

    private ProgramReasonResponse buildFallbackResponse(
            ProgramReasonAiRequest request,
            ScoreBreakdownDto score
    ) {
        List<String> reasons = new ArrayList<>();

        if (score.getReasonCodes() != null) {
            if (score.getReasonCodes().contains(ReasonCode.AGE_FIT)) {
                reasons.add("자녀 연령 조건과 프로그램 대상 연령이 잘 맞아 참여하기 좋아요.");
            }

            if (score.getReasonCodes().contains(ReasonCode.KEYWORD_MATCH)) {
                reasons.add("아이의 관심 키워드와 프로그램 특성이 잘 맞는 편이에요.");
            }

            if (score.getReasonCodes().contains(ReasonCode.BUDGET_FIT)) {
                if (request.program() != null && Boolean.TRUE.equals(request.program().isFree())) {
                    reasons.add("무료 프로그램이라 비용 부담 없이 시작해볼 수 있어요.");
                } else {
                    reasons.add("보호자가 설정한 예산 조건 안에서 검토하기 좋은 프로그램이에요.");
                }
            }

            if (score.getReasonCodes().contains(ReasonCode.HIGH_RATING)) {
                reasons.add("평점과 후기 수를 함께 봤을 때 만족도 기준에서 비교해볼 만해요.");
            }
        }

        if (reasons.isEmpty()) {
            reasons.add("추천 점수와 보호자 조건을 기준으로 검토할 만한 프로그램이에요.");
        }

        return new ProgramReasonResponse(
                score.getTotalScore() != null ? score.getTotalScore().doubleValue() : 0.0,
                reasons.stream().limit(4).toList(),
                "FALLBACK"
        );
    }

    private boolean isValidAiResponse(ProgramReasonResponse response) {
        return response != null
                && response.reasonList() != null
                && !response.reasonList().isEmpty()
                && response.matchScore() != null;
    }

    private String buildCacheKey(Long programId, Long preferenceId, ProgramReasonAiRequest request) {
        return "ai:program-reason:" + preferenceId + ":" + programId + ":" + sha256(writeRequest(request));
    }

    private String writeRequest(ProgramReasonAiRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private String writeResponse(ProgramReasonResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private ProgramReasonResponse readCachedResponse(String cachedValue) {
        try {
            return objectMapper.readValue(cachedValue, ProgramReasonResponse.class);
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

    private int calculateAge(ChildProfile child) {
        if (child.getBirthDate() == null) {
            return 0;
        }

        return Period.between(child.getBirthDate(), LocalDate.now()).getYears();
    }

    private Double toDouble(java.math.BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }

    private String enumName(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    private String trim(String value) {
        return value != null ? value.trim().replaceAll("\\s+", " ") : "";
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}

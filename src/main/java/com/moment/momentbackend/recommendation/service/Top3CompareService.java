package com.moment.momentbackend.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.momentbackend.child.entity.ChildConcern;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.child.repository.ChildConcernRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.global.redis.RedisService;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.client.Top3CompareAiClient;
import com.moment.momentbackend.recommendation.dto.Top3CompareAiRequest;
import com.moment.momentbackend.recommendation.dto.Top3CompareChildRequest;
import com.moment.momentbackend.recommendation.dto.Top3CompareItemResponse;
import com.moment.momentbackend.recommendation.dto.Top3ComparePreferenceRequest;
import com.moment.momentbackend.recommendation.dto.Top3CompareProgramRequest;
import com.moment.momentbackend.recommendation.dto.Top3CompareResponse;
import com.moment.momentbackend.recommendation.entity.AiRecommendation;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.repository.AiRecommendationRepository;
import com.moment.momentbackend.recommendation.repository.RecommendationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Top3CompareService {

    private static final int REQUIRED_TOP3_COUNT = 3;
    private static final long CACHE_TTL_SECONDS = 60 * 60 * 24;

    private final RecommendationPreferenceRepository preferenceRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final ProgramRepository programRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ChildConcernRepository childConcernRepository;
    private final Top3CompareAiClient top3CompareAiClient;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Top3CompareResponse compare(Long userId, Long preferenceId) {
        validateUserId(userId);

        RecommendationPreference preference = preferenceRepository.findByIdAndUserId(preferenceId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PREFERENCE_NOT_FOUND));

        ChildProfile child = childProfileRepository.findByIdAndUserId(preference.getChildId(), userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        List<AiRecommendation> top3Recommendations = aiRecommendationRepository
                .findAllByUserIdAndPreferenceIdAndIsTop3TrueOrderByRankNoAsc(userId, preferenceId);

        if (top3Recommendations.size() < REQUIRED_TOP3_COUNT) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        Top3CompareAiRequest aiRequest = buildAiRequest(child, preference, top3Recommendations);
        String cacheKey = buildCacheKey(preferenceId, aiRequest);

        return redisService.getValue(cacheKey)
                .map(this::readCachedResponse)
                .orElseGet(() -> generateAndCache(cacheKey, aiRequest));
    }

    private Top3CompareResponse generateAndCache(
            String cacheKey,
            Top3CompareAiRequest aiRequest
    ) {
        Top3CompareResponse response = top3CompareAiClient.compare(aiRequest)
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

    private Top3CompareAiRequest buildAiRequest(
            ChildProfile child,
            RecommendationPreference preference,
            List<AiRecommendation> top3Recommendations
    ) {
        List<String> concerns = childConcernRepository.findByChildProfileId(child.getId()).stream()
                .map(ChildConcern::getConcern)
                .filter(Objects::nonNull)
                .filter(concern -> !concern.isBlank())
                .toList();

        Map<Long, Program> programMap = findProgramMap(top3Recommendations);

        List<Top3CompareProgramRequest> programs = top3Recommendations.stream()
                .sorted(Comparator.comparing(AiRecommendation::getRankNo))
                .map(recommendation -> {
                    Program program = programMap.get(recommendation.getProgramId());

                    if (program == null) {
                        throw new CustomException(ErrorCode.PROGRAM_NOT_FOUND);
                    }

                    return new Top3CompareProgramRequest(
                            program.getId(),
                            trim(program.getTitle()),
                            trim(program.getCategory()),
                            trim(program.getDescription()),
                            trim(program.getRegion()),
                            program.getPrice(),
                            program.getIsFree(),
                            trim(program.getClassType()),
                            program.getRatingAvg() != null ? program.getRatingAvg().doubleValue() : null,
                            program.getReviewCount(),
                            recommendation.getRankNo(),
                            recommendation.getTotalScore() != null ? recommendation.getTotalScore().doubleValue() : 0.0,
                            trim(recommendation.getRecommendReason()),
                            buildScoreBreakdown(recommendation)
                    );
                })
                .toList();

        return new Top3CompareAiRequest(
                new Top3CompareChildRequest(
                        child.getId(),
                        calculateAge(child),
                        concerns
                ),
                new Top3ComparePreferenceRequest(
                        preference.getId(),
                        trim(preference.getRegion()),
                        enumName(preference.getMonthlyBudget()),
                        enumName(preference.getTransportType()),
                        enumName(preference.getMoveTime()),
                        enumName(preference.getOnlinePreference()),
                        enumName(preference.getClassType())
                ),
                programs
        );
    }

    private Map<Long, Program> findProgramMap(List<AiRecommendation> recommendations) {
        List<Long> programIds = recommendations.stream()
                .map(AiRecommendation::getProgramId)
                .toList();

        Map<Long, Program> programMap = new LinkedHashMap<>();

        for (Program program : programRepository.findAllById(programIds)) {
            programMap.put(program.getId(), program);
        }

        return programMap;
    }

    private Map<String, Double> buildScoreBreakdown(AiRecommendation recommendation) {
        Map<String, Double> scoreBreakdown = new LinkedHashMap<>();

        scoreBreakdown.put("scoreDistance", toDouble(recommendation.getScoreDistance()));
        scoreBreakdown.put("scoreBudget", toDouble(recommendation.getScoreBudget()));
        scoreBreakdown.put("scoreAge", toDouble(recommendation.getScoreAge()));
        scoreBreakdown.put("scoreKeyword", toDouble(recommendation.getScoreKeyword()));
        scoreBreakdown.put("scoreClassType", toDouble(recommendation.getScoreClassType()));
        scoreBreakdown.put("scoreRecruiting", toDouble(recommendation.getScoreRecruiting()));
        scoreBreakdown.put("scoreReview", toDouble(recommendation.getScoreReview()));

        return scoreBreakdown;
    }

    private Top3CompareResponse buildFallbackResponse(Top3CompareAiRequest request) {
        List<Top3CompareItemResponse> items = new ArrayList<>();

        for (Top3CompareProgramRequest program : request.programs()) {
            items.add(new Top3CompareItemResponse(
                    program.programId(),
                    buildFallbackReason(program),
                    buildFallbackTag(program)
            ));
        }

        return new Top3CompareResponse(
                "추천 점수와 보호자 조건을 기준으로 TOP3 프로그램을 비교했어요.",
                items,
                "FALLBACK"
        );
    }

    private String buildFallbackReason(Top3CompareProgramRequest program) {
        if (Boolean.TRUE.equals(program.isFree())) {
            return trim(program.title()) + "은(는) 비용 부담이 적고 추천 조건에 잘 맞는 프로그램이에요.";
        }

        if (program.ratingAvg() != null && program.ratingAvg() >= 4.5) {
            return trim(program.title()) + "은(는) 후기 만족도가 높아 우선 비교해볼 만한 프로그램이에요.";
        }

        return trim(program.title()) + "은(는) 추천 점수와 선호 조건을 기준으로 비교할 만한 프로그램이에요.";
    }

    private String buildFallbackTag(Top3CompareProgramRequest program) {
        if (Boolean.TRUE.equals(program.isFree())) {
            return "비용 부담↓";
        }

        if (program.category() != null && !program.category().isBlank()) {
            return trim(program.category());
        }

        return "맞춤 추천";
    }

    private boolean isValidAiResponse(Top3CompareResponse response) {
        return response != null
                && response.commonSummary() != null
                && !response.commonSummary().isBlank()
                && response.items() != null
                && !response.items().isEmpty();
    }

    private String buildCacheKey(Long preferenceId, Top3CompareAiRequest request) {
        return "ai:compare:" + preferenceId + ":" + sha256(writeRequest(request));
    }

    private String writeRequest(Top3CompareAiRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private String writeResponse(Top3CompareResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private Top3CompareResponse readCachedResponse(String cachedValue) {
        try {
            return objectMapper.readValue(cachedValue, Top3CompareResponse.class);
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

    private Integer calculateAge(ChildProfile child) {
        if (child.getBirthDate() == null) {
            return null;
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

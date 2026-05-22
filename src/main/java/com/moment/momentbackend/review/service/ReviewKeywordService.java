package com.moment.momentbackend.review.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.global.redis.RedisService;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.review.client.ReviewKeywordAiClient;
import com.moment.momentbackend.review.dto.ReviewKeywordAiRequest;
import com.moment.momentbackend.review.dto.ReviewKeywordAiResponse;
import com.moment.momentbackend.review.dto.ReviewKeywordProgramRequest;
import com.moment.momentbackend.review.dto.ReviewKeywordResponse;
import com.moment.momentbackend.review.dto.ReviewKeywordStatsRequest;
import com.moment.momentbackend.review.entity.Review;
import com.moment.momentbackend.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewKeywordService {

    private static final long CACHE_TTL_SECONDS = 60 * 60 * 24;
    private static final int MAX_POSITIVE_KEYWORD_COUNT = 5;
    private static final int MAX_NEGATIVE_KEYWORD_COUNT = 3;
    private static final int MAX_REVIEW_TEXT_COUNT = 20;

    private static final Map<String, List<String>> POSITIVE_KEYWORD_RULES = Map.ofEntries(
            Map.entry("선생님 친절", List.of("선생님", "친절", "피드백", "꼼꼼", "성향")),
            Map.entry("소규모 수업", List.of("소규모", "집중", "케어")),
            Map.entry("만족도 높음", List.of("만족", "좋아해", "즐거워", "강추", "다음 기수")),
            Map.entry("커리큘럼 체계적", List.of("커리큘럼", "체계적")),
            Map.entry("프로젝트 중심", List.of("프로젝트", "중심")),
            Map.entry("온라인 집중", List.of("온라인", "집중")),
            Map.entry("위치 가까움", List.of("위치", "가깝"))
    );

    private static final Map<String, List<String>> NEGATIVE_KEYWORD_RULES = Map.ofEntries(
            Map.entry("집중 어려움", List.of("집중이 어려", "산만", "지루")),
            Map.entry("거리 부담", List.of("멀", "거리", "이동 부담")),
            Map.entry("수업 난이도 높음", List.of("어려", "난이도", "힘들")),
            Map.entry("피드백 부족", List.of("피드백 부족", "설명 부족"))
    );

    private final ProgramRepository programRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewKeywordAiClient reviewKeywordAiClient;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ReviewKeywordResponse generate(Long programId) {
        Program program = programRepository.findDetailById(programId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROGRAM_NOT_FOUND));

        String cacheKey = buildCacheKey(programId);

        return redisService.getValue(cacheKey)
                .map(this::readCachedResponse)
                .orElseGet(() -> generateAndCache(cacheKey, program));
    }

    private ReviewKeywordResponse generateAndCache(String cacheKey, Program program) {
        List<Review> reviews = reviewRepository.findByProgramIdOrderByCreatedAtDesc(program.getId());

        if (reviews.isEmpty()) {
            return buildNoReviewFallback(program);
        }

        ReviewKeywordAiRequest aiRequest = buildAiRequest(program, reviews);

        ReviewKeywordResponse response = reviewKeywordAiClient.generate(aiRequest)
                .filter(this::isValidAiResponse)
                .map(aiResponse -> toResponse(program.getId(), aiRequest, aiResponse))
                .orElseGet(() -> buildFallbackResponse(program.getId(), aiRequest));

        if (!"FALLBACK".equals(response.source())) {
            redisService.setValue(cacheKey, writeResponse(response), CACHE_TTL_SECONDS);
        }

        return response;
    }

    private ReviewKeywordAiRequest buildAiRequest(Program program, List<Review> reviews) {
        List<String> reviewTexts = reviews.stream()
                .map(Review::getContent)
                .filter(Objects::nonNull)
                .map(this::normalizeText)
                .filter(text -> !text.isBlank())
                .limit(MAX_REVIEW_TEXT_COUNT)
                .toList();

        List<String> positiveKeywords = extractKeywords(reviewTexts, POSITIVE_KEYWORD_RULES, MAX_POSITIVE_KEYWORD_COUNT);
        List<String> negativeKeywords = extractKeywords(reviewTexts, NEGATIVE_KEYWORD_RULES, MAX_NEGATIVE_KEYWORD_COUNT);

        return new ReviewKeywordAiRequest(
                new ReviewKeywordProgramRequest(
                        program.getId(),
                        normalizeText(program.getTitle()),
                        normalizeText(program.getCategory()),
                        program.getRatingAvg() != null ? program.getRatingAvg().doubleValue() : null,
                        program.getReviewCount()
                ),
                new ReviewKeywordStatsRequest(
                        reviews.size(),
                        calculateAverageRating(reviews),
                        buildRatingDistribution(reviews),
                        positiveKeywords,
                        negativeKeywords,
                        reviewTexts
                )
        );
    }

    private ReviewKeywordResponse toResponse(
            Long programId,
            ReviewKeywordAiRequest request,
            ReviewKeywordAiResponse aiResponse
    ) {
        List<String> positiveKeywords = normalizeKeywords(
                aiResponse.positiveKeywords(),
                request.stats().positiveKeywords(),
                MAX_POSITIVE_KEYWORD_COUNT
        );
        List<String> negativeKeywords = normalizeKeywords(
                aiResponse.negativeKeywords(),
                request.stats().negativeKeywords(),
                MAX_NEGATIVE_KEYWORD_COUNT
        );

        return new ReviewKeywordResponse(
                programId,
                request.stats().reviewCount(),
                request.stats().ratingAverage(),
                positiveKeywords,
                negativeKeywords,
                normalizeSummary(aiResponse.summary(), request, positiveKeywords, negativeKeywords),
                aiResponse.source() != null && !aiResponse.source().isBlank() ? aiResponse.source() : "OPENAI"
        );
    }

    private ReviewKeywordResponse buildFallbackResponse(Long programId, ReviewKeywordAiRequest request) {
        return new ReviewKeywordResponse(
                programId,
                request.stats().reviewCount(),
                request.stats().ratingAverage(),
                request.stats().positiveKeywords(),
                request.stats().negativeKeywords(),
                buildSummary(request.stats().reviewCount(), request.stats().positiveKeywords(), request.stats().negativeKeywords()),
                "FALLBACK"
        );
    }

    private ReviewKeywordResponse buildNoReviewFallback(Program program) {
        return new ReviewKeywordResponse(
                program.getId(),
                0,
                null,
                List.of(),
                List.of(),
                "아직 등록된 후기가 없어 AI 키워드 분석을 준비 중이에요.",
                "FALLBACK"
        );
    }

    private boolean isValidAiResponse(ReviewKeywordAiResponse response) {
        return response != null
                && response.summary() != null
                && !response.summary().isBlank()
                && response.positiveKeywords() != null
                && response.negativeKeywords() != null;
    }

    private List<String> extractKeywords(
            List<String> reviewTexts,
            Map<String, List<String>> rules,
            int limit
    ) {
        LinkedHashSet<String> keywords = new LinkedHashSet<>();

        for (Map.Entry<String, List<String>> entry : rules.entrySet()) {
            String keyword = entry.getKey();
            List<String> signals = entry.getValue();

            boolean matched = reviewTexts.stream()
                    .anyMatch(text -> containsAny(text, signals));

            if (matched) {
                keywords.add(keyword);
            }

            if (keywords.size() >= limit) {
                break;
            }
        }

        return new ArrayList<>(keywords);
    }

    private boolean containsAny(String text, List<String> signals) {
        String normalizedText = normalizeText(text).replace(" ", "");

        return signals.stream()
                .map(this::normalizeText)
                .map(signal -> signal.replace(" ", ""))
                .anyMatch(normalizedText::contains);
    }

    private List<String> normalizeKeywords(List<String> keywords, List<String> allowedKeywords, int limit) {
        if (keywords == null || allowedKeywords == null || allowedKeywords.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> allowed = new LinkedHashSet<>(allowedKeywords);
        List<String> result = new ArrayList<>();

        for (String keyword : keywords) {
            String normalized = normalizeText(keyword);

            if (allowed.contains(normalized) && !result.contains(normalized)) {
                result.add(normalized);
            }

            if (result.size() >= limit) {
                break;
            }
        }

        return result;
    }

    private Map<String, Integer> buildRatingDistribution(List<Review> reviews) {
        Map<String, Integer> distribution = new LinkedHashMap<>();

        distribution.put("5", 0);
        distribution.put("4", 0);
        distribution.put("3", 0);
        distribution.put("2", 0);
        distribution.put("1", 0);

        for (Review review : reviews) {
            int bucket = toRatingBucket(review.getRating());
            distribution.computeIfPresent(String.valueOf(bucket), (key, count) -> count + 1);
        }

        return distribution;
    }

    private int toRatingBucket(BigDecimal rating) {
        if (rating == null) {
            return 1;
        }

        int bucket = rating.setScale(0, java.math.RoundingMode.DOWN).intValue();
        return Math.max(1, Math.min(5, bucket));
    }

    private Double calculateAverageRating(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return null;
        }

        return reviews.stream()
                .map(Review::getRating)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);
    }

    private String normalizeSummary(
            String summary,
            ReviewKeywordAiRequest request,
            List<String> positiveKeywords,
            List<String> negativeKeywords
    ) {
        String normalized = normalizeText(summary);

        if (normalized.isBlank()) {
            return buildSummary(request.stats().reviewCount(), positiveKeywords, negativeKeywords);
        }

        normalized = normalized.replace("아이의만족도", "아이의 만족도");
        normalized = normalized.replace("높은만족도", "높은 만족도");

        if (normalized.length() > 120) {
            normalized = normalized.substring(0, 120).trim();
        }

        return normalized;
    }

    private String buildSummary(int reviewCount, List<String> positiveKeywords, List<String> negativeKeywords) {
        if (reviewCount <= 0) {
            return "아직 등록된 후기가 없어 AI 키워드 분석을 준비 중이에요.";
        }

        if (positiveKeywords != null && !positiveKeywords.isEmpty()) {
            return "등록된 후기를 기준으로 아이 반응과 수업 만족도가 긍정적으로 나타났어요.";
        }

        if (negativeKeywords != null && !negativeKeywords.isEmpty()) {
            return "등록된 후기에서 개선이 필요한 의견도 일부 확인됐어요.";
        }

        return "등록된 후기를 바탕으로 전반적인 만족도를 확인할 수 있어요.";
    }

    private String buildCacheKey(Long programId) {
        return "ai:review-keywords:" + programId;
    }

    private String writeResponse(ReviewKeywordResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private ReviewKeywordResponse readCachedResponse(String cachedValue) {
        try {
            return objectMapper.readValue(cachedValue, ReviewKeywordResponse.class);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private String normalizeText(String value) {
        return value != null ? value.trim().replaceAll("\\s+", " ") : "";
    }
}

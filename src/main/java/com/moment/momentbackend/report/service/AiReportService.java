package com.moment.momentbackend.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.momentbackend.global.redis.RedisService;
import com.moment.momentbackend.report.client.ParentingReportAiClient;
import com.moment.momentbackend.report.dto.ParentingReportAiRequest;
import com.moment.momentbackend.report.dto.ParentingReportAiResponse;
import com.moment.momentbackend.report.dto.ParentingReportChildRequest;
import com.moment.momentbackend.report.dto.ParentingReportGenerateResponse;
import com.moment.momentbackend.report.dto.ParentingReportSavingsRequest;
import com.moment.momentbackend.benefit.entity.BenefitMatch;
import com.moment.momentbackend.benefit.repository.BenefitMatchRepository;
import com.moment.momentbackend.child.repository.ChildProfileRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.recommendation.repository.AiRecommendationRepository;
import com.moment.momentbackend.report.dto.AiReportResponseDto;
import com.moment.momentbackend.report.entity.AiReport;
import com.moment.momentbackend.report.repository.AiReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moment.momentbackend.recommendation.repository.RecommendationPreferenceRepository;
import com.moment.momentbackend.child.entity.ChildProfile;
import com.moment.momentbackend.report.dto.ParentingRawReportResponseDto;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class AiReportService {

    private static final long PARENTING_REPORT_CACHE_TTL_SECONDS = 60 * 60 * 24;

    private final AiReportRepository aiReportRepository;
    private final ChildProfileRepository childProfileRepository;
    private final BenefitMatchRepository benefitMatchRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final ProgramRepository programRepository;
    private final RecommendationPreferenceRepository preferenceRepository;
    private final ParentingReportAiClient parentingReportAiClient;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @Transactional
    public AiReportResponseDto generateReport(Long userId, Long childId) {
        // 본인 자녀 소유권 확인
        childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        // 리포트 데이터 수집
        List<BenefitMatch> matches = benefitMatchRepository.findAllByChildIdWithBenefit(childId);

        int totalSupportCount = (int) matches.stream()
                .filter(m -> "APPLICABLE".equals(m.getMatchStatus()))
                .count();

        int totalMonthlySaving = matches.stream()
                .filter(m -> "APPLICABLE".equals(m.getMatchStatus()))
                .mapToInt(m -> m.getExpectedMonthlySaving() != null ? m.getExpectedMonthlySaving() : 0)
                .sum();

        int totalFreeProgramCount = (int) programRepository.countByIsFreeTrueAndIsPublicTrue();
        int totalRecommendCount = (int) aiRecommendationRepository.countByChildId(childId);

        BigDecimal aiMatchScore = calcMatchScore(totalSupportCount, totalMonthlySaving, totalRecommendCount);
        String summaryMessage = buildSummaryMessage(totalSupportCount, totalMonthlySaving,
                totalFreeProgramCount, totalRecommendCount, aiMatchScore);

        // 기존 리포트 있으면 덮어쓰기, 없으면 신규 저장
        Optional<AiReport> existing = aiReportRepository.findByChildId(childId);

        AiReport report;
        if (existing.isPresent()) {
            report = existing.get();
            report.update(totalSupportCount, totalFreeProgramCount, totalRecommendCount,
                    totalMonthlySaving, aiMatchScore, summaryMessage);
        } else {
            report = AiReport.builder()
                    .childId(childId)
                    .totalSupportCount(totalSupportCount)
                    .totalFreeProgramCount(totalFreeProgramCount)
                    .totalRecommendCount(totalRecommendCount)
                    .totalMonthlySaving(totalMonthlySaving)
                    .aiMatchScore(aiMatchScore)
                    .summaryMessage(summaryMessage)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        return AiReportResponseDto.from(aiReportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public AiReportResponseDto getReport(Long userId, Long childId) {
        childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        AiReport report = aiReportRepository.findByChildId(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return AiReportResponseDto.from(report);
    }

    // rule-based 점수 계산
    private BigDecimal calcMatchScore(int supportCount, int monthlySaving, int recommendCount) {
        double score = 20.0;
        if (supportCount >= 3) score += 30.0;
        else if (supportCount >= 1) score += 15.0;

        if (monthlySaving >= 100000) score += 20.0;
        else if (monthlySaving >= 50000) score += 10.0;

        if (recommendCount >= 5) score += 20.0;
        else if (recommendCount >= 1) score += 10.0;

        score = Math.min(score, 100.0);
        return BigDecimal.valueOf(score);
    }

    // rule-based 요약 메시지 생성
    private String buildSummaryMessage(int supportCount, int monthlySaving,
                                       int freeProgramCount, int recommendCount,
                                       BigDecimal matchScore) {
        StringBuilder sb = new StringBuilder();
        sb.append("현재 자녀에게 적합한 지원 혜택이 ").append(supportCount).append("건 확인되었습니다. ");

        if (monthlySaving > 0) {
            sb.append("월 최대 ").append(String.format("%,d", monthlySaving))
                    .append("원의 절감이 가능합니다. ");
        }

        if (freeProgramCount > 0) {
            sb.append("현재 무료 프로그램 ").append(freeProgramCount).append("개가 운영 중입니다. ");
        }

        if (recommendCount > 0) {
            sb.append("추천된 프로그램 ").append(recommendCount).append("개를 확인해보세요. ");
        }

        sb.append("(종합 매칭 점수: ").append(matchScore).append("점)");
        return sb.toString();
    }

    @Transactional
    public ParentingReportGenerateResponse generateParentingReport(Long userId, Long childId) {
        ParentingRawReportResponseDto rawReport = getRawReport(userId, childId);
        ParentingReportAiRequest aiRequest = buildParentingReportAiRequest(rawReport);
        String cacheKey = buildParentingReportCacheKey(childId, aiRequest);

        ParentingReportAiResponse aiResponse = redisService.getValue(cacheKey)
                .map(this::readCachedParentingReportResponse)
                .filter(this::isValidParentingReportAiResponse)
                .orElseGet(() -> generateAndCacheParentingReport(cacheKey, aiRequest));

        Integer totalMonthlySaving = safeInt(rawReport.getSavingsBreakdown().getTotalMonthlySaving());
        BigDecimal aiMatchScore = calcMatchScore(
                safeInt(rawReport.getSupportCount()),
                totalMonthlySaving,
                safeInt(rawReport.getRecommendCount())
        );

        AiReport savedReport = saveOrUpdateReport(
                childId,
                safeInt(rawReport.getSupportCount()),
                safeInt(rawReport.getFreeProgramCount()),
                safeInt(rawReport.getRecommendCount()),
                totalMonthlySaving,
                aiMatchScore,
                aiResponse.summaryMessage()
        );

        return new ParentingReportGenerateResponse(
                childId,
                safeInt(rawReport.getSupportCount()),
                safeInt(rawReport.getFreeProgramCount()),
                safeInt(rawReport.getRecommendCount()),
                totalMonthlySaving,
                aiMatchScore,
                aiResponse.summaryMessage(),
                aiResponse.savingMessage(),
                aiResponse.benefitMessage(),
                aiResponse.recommendationMessage(),
                normalizeSource(aiResponse.source()),
                savedReport.getCreatedAt(),
                savedReport.getUpdatedAt()
        );
    }

    private ParentingReportAiResponse generateAndCacheParentingReport(
            String cacheKey,
            ParentingReportAiRequest aiRequest
    ) {
        ParentingReportAiResponse response = parentingReportAiClient.generate(aiRequest)
                .filter(this::isValidParentingReportAiResponse)
                .orElseGet(() -> buildParentingReportFallback(aiRequest));

        if (!"FALLBACK".equals(normalizeSource(response.source()))) {
            redisService.setValue(
                    cacheKey,
                    writeParentingReportResponse(response),
                    PARENTING_REPORT_CACHE_TTL_SECONDS
            );
        }

        return response;
    }

    private ParentingReportAiRequest buildParentingReportAiRequest(ParentingRawReportResponseDto rawReport) {
        ParentingRawReportResponseDto.ChildInfoDto childInfo = rawReport.getChildInfo();
        ParentingRawReportResponseDto.SavingsBreakdownDto savings = rawReport.getSavingsBreakdown();

        return new ParentingReportAiRequest(
                new ParentingReportChildRequest(
                        trim(childInfo.getChildName()),
                        childInfo.getAge(),
                        childInfo.getConcerns() != null ? childInfo.getConcerns() : Collections.emptyList(),
                        trim(childInfo.getRegion()),
                        trim(childInfo.getMonthlyBudget())
                ),
                safeInt(rawReport.getSupportCount()),
                safeInt(rawReport.getFreeProgramCount()),
                safeInt(rawReport.getRecommendCount()),
                new ParentingReportSavingsRequest(
                        safeInt(savings.getChildcareSupportAmount()),
                        safeInt(savings.getEducationVoucherAmount()),
                        safeInt(savings.getFreeProgramAmount()),
                        safeInt(savings.getTotalMonthlySaving())
                ),
                trim(rawReport.getCalculationBasis())
        );
    }

    private AiReport saveOrUpdateReport(
            Long childId,
            Integer totalSupportCount,
            Integer totalFreeProgramCount,
            Integer totalRecommendCount,
            Integer totalMonthlySaving,
            BigDecimal aiMatchScore,
            String summaryMessage
    ) {
        Optional<AiReport> existing = aiReportRepository.findByChildId(childId);

        AiReport report;
        if (existing.isPresent()) {
            report = existing.get();
            report.update(
                    totalSupportCount,
                    totalFreeProgramCount,
                    totalRecommendCount,
                    totalMonthlySaving,
                    aiMatchScore,
                    summaryMessage
            );
        } else {
            report = AiReport.builder()
                    .childId(childId)
                    .totalSupportCount(totalSupportCount)
                    .totalFreeProgramCount(totalFreeProgramCount)
                    .totalRecommendCount(totalRecommendCount)
                    .totalMonthlySaving(totalMonthlySaving)
                    .aiMatchScore(aiMatchScore)
                    .summaryMessage(summaryMessage)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        return aiReportRepository.save(report);
    }

    private ParentingReportAiResponse buildParentingReportFallback(ParentingReportAiRequest request) {
        String childName = request.childInfo() != null && request.childInfo().childName() != null
                && !request.childInfo().childName().isBlank()
                ? request.childInfo().childName()
                : "아이";

        int totalMonthlySaving = request.savingsBreakdown() != null
                ? safeInt(request.savingsBreakdown().totalMonthlySaving())
                : 0;

        String summaryMessage = request.supportCount() != null && request.supportCount() > 0
                ? childName + "에게 맞는 육아 지원 혜택을 찾았어요."
                : childName + "의 조건을 기준으로 이용 가능한 육아 지원 정보를 정리했어요.";

        String savingMessage = totalMonthlySaving > 0
                ? "현재 조건으로 월 평균 " + formatWon(totalMonthlySaving) + " 절감 가능한 경로가 있어요."
                : "현재 조건에서 확인 가능한 절감 경로를 계속 찾아볼게요.";

        String benefitMessage = safeInt(request.supportCount()) > 0 || safeInt(request.freeProgramCount()) > 0
                ? "지원금과 무료 공공서비스를 함께 활용한 기준이에요."
                : "자녀 조건과 지역 정보를 기준으로 지원 혜택을 분석했어요.";

        String recommendationMessage = safeInt(request.recommendCount()) > 0
                ? "맞춤 추천 프로그램 " + request.recommendCount() + "개도 함께 확인해보면 좋아요."
                : "자녀 조건을 등록하면 맞춤 추천 프로그램을 더 정확히 확인할 수 있어요.";

        return new ParentingReportAiResponse(
                summaryMessage,
                savingMessage,
                benefitMessage,
                recommendationMessage,
                "FALLBACK"
        );
    }

    private boolean isValidParentingReportAiResponse(ParentingReportAiResponse response) {
        return response != null
                && response.summaryMessage() != null
                && !response.summaryMessage().isBlank()
                && response.savingMessage() != null
                && !response.savingMessage().isBlank()
                && response.benefitMessage() != null
                && !response.benefitMessage().isBlank()
                && response.recommendationMessage() != null
                && !response.recommendationMessage().isBlank();
    }

    private String buildParentingReportCacheKey(Long childId, ParentingReportAiRequest request) {
        return "ai:report:parenting:" + childId + ":" + sha256(writeParentingReportRequest(request));
    }

    private String writeParentingReportRequest(ParentingReportAiRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private String writeParentingReportResponse(ParentingReportAiResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private ParentingReportAiResponse readCachedParentingReportResponse(String cachedValue) {
        try {
            return objectMapper.readValue(cachedValue, ParentingReportAiResponse.class);
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

    private String normalizeSource(String source) {
        return source != null && !source.isBlank() ? source : "OPENAI";
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    private String trim(String value) {
        return value != null ? value.trim().replaceAll("\\s+", " ") : null;
    }

    private String formatWon(int amount) {
        if (amount >= 10_000 && amount % 10_000 == 0) {
            return (amount / 10_000) + "만원";
        }

        return String.format("%,d원", amount);
    }

    @Transactional(readOnly = true)
    public ParentingRawReportResponseDto getRawReport(Long userId, Long childId) {

        // 1. 자녀 프로필 조회 (소유권 확인 포함)
        ChildProfile child = childProfileRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_ACCESS_DENIED));

        // 2. 나이 계산
        int age = Period.between(child.getBirthDate(), LocalDate.now()).getYears();

        // 3. 관심분야
        List<String> concerns = child.getConcerns().stream()
                .map(c -> c.getConcern())
                .toList();

        // 4. 지역, 예산 (추천 선호도에서 조회, 없으면 null 처리)
        String region = null;
        String monthlyBudget = null;
        var preferenceOpt = preferenceRepository.findTopByChildIdOrderByCreatedAtDesc(childId);
        if (preferenceOpt.isPresent()) {
            region = preferenceOpt.get().getRegion();
            monthlyBudget = preferenceOpt.get().getMonthlyBudget() != null
                    ? preferenceOpt.get().getMonthlyBudget().name() : null;
        }

        // 5. 지원금 데이터
        List<BenefitMatch> matches = benefitMatchRepository.findAllByChildIdWithBenefit(childId);
        List<BenefitMatch> applicable = matches.stream()
                .filter(m -> "APPLICABLE".equals(m.getMatchStatus()))
                .toList();

        int supportCount = applicable.size();

        // 6. 절감액 세부 분류 (BenefitType 기준)
        int childcareAmount = applicable.stream()
                .filter(m -> m.getBenefit().getBenefitType() != null
                        && m.getBenefit().getBenefitType().contains("돌봄"))
                .mapToInt(m -> m.getExpectedMonthlySaving() != null ? m.getExpectedMonthlySaving() : 0)
                .sum();

        int educationAmount = applicable.stream()
                .filter(m -> m.getBenefit().getBenefitType() != null
                        && (m.getBenefit().getBenefitType().contains("교육")
                        || m.getBenefit().getBenefitType().contains("바우처")))
                .mapToInt(m -> m.getExpectedMonthlySaving() != null ? m.getExpectedMonthlySaving() : 0)
                .sum();

        // 무료 프로그램 활용액: 프로그램 1개당 20,000원 절감 추정
        int freeProgramCount = (int) programRepository.countByIsFreeTrueAndIsPublicTrue();
        int freeProgramAmount = freeProgramCount * 20_000;

        int totalMonthlySaving = childcareAmount + educationAmount + freeProgramAmount;

        // 7. 추천 개수
        int recommendCount = (int) aiRecommendationRepository.countByChildId(childId);

        // 8. 계산 근거 메시지
        String calculationBasis = String.format(
                "아이돌봄 지원금 %,d원 + 교육비 바우처 %,d원 + 무료 프로그램 %d개(개당 20,000원 추정) %,d원 = 월 최대 %,d원 절감 예상",
                childcareAmount, educationAmount, freeProgramCount, freeProgramAmount, totalMonthlySaving
        );

        return ParentingRawReportResponseDto.builder()
                .childInfo(ParentingRawReportResponseDto.ChildInfoDto.builder()
                        .childName(child.getChildName())
                        .age(age)
                        .concerns(concerns)
                        .region(region)
                        .monthlyBudget(monthlyBudget)
                        .build())
                .supportCount(supportCount)
                .freeProgramCount(freeProgramCount)
                .recommendCount(recommendCount)
                .savingsBreakdown(ParentingRawReportResponseDto.SavingsBreakdownDto.builder()
                        .childcareSupportAmount(childcareAmount)
                        .educationVoucherAmount(educationAmount)
                        .freeProgramAmount(freeProgramAmount)
                        .totalMonthlySaving(totalMonthlySaving)
                        .build())
                .calculationBasis(calculationBasis)
                .build();
    }
}
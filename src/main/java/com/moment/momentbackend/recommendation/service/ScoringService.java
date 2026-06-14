package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.recommendation.dto.ScoreBreakdownDto;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.ReasonCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Service
public class ScoringService {

    private static final double W_DISTANCE   = 20.0; // 지역/거리 적합도 20%
    private static final double W_BUDGET     = 20.0; // 예산/무료 여부 20%
    private static final double W_AGE        = 15.0; // 연령 적합도 15%
    private static final double W_KEYWORD    = 15.0; // 고민 키워드 적합도 15%
    private static final double W_CLASS_TYPE = 10.0; // 수업 환경 적합도 10%
    private static final double W_RECRUITING = 10.0; // 신청 가능 여부 10%
    private static final double W_REVIEW     = 10.0; // 후기 만족도 10%

    public ScoreBreakdownDto calculate(Program program, RecommendationPreference preference,
                                       int childAge, double userLat, double userLon,
                                       List<String> concerns) {

        double scoreDistance  = calcDistanceScore(program, userLat, userLon);
        double scoreBudget    = calcBudgetScore(program, preference);
        double scoreAge       = calcAgeScore(program, childAge);
        double scoreKeyword   = calcKeywordScore(program, concerns);
        double scoreClassType = calcClassTypeScore(program, preference);
        double scoreRecruiting = Boolean.TRUE.equals(program.getIsRecruiting()) ? W_RECRUITING : 0.0;
        double scoreReview    = calcReviewScore(program);

        double total = scoreDistance + scoreBudget + scoreAge + scoreKeyword
                + scoreClassType + scoreRecruiting + scoreReview;
        total = Math.min(total, 100.0);

        List<ReasonCode> reasonCodes = buildReasonCodes(
                scoreDistance, scoreBudget, scoreAge, scoreKeyword,
                scoreClassType, scoreRecruiting, scoreReview);

        return ScoreBreakdownDto.builder()
                .scoreDistance(bd(scoreDistance))
                .scoreBudget(bd(scoreBudget))
                .scoreAge(bd(scoreAge))
                .scoreKeyword(bd(scoreKeyword))
                .scoreClassType(bd(scoreClassType))
                .scoreRecruiting(bd(scoreRecruiting))
                .scoreReview(bd(scoreReview))
                .totalScore(bd(total))
                .reasonCodes(reasonCodes)
                .build();
    }
    private double calcDistanceScore(Program program, double userLat, double userLon) {
        if (program.getLatitude() == null || program.getLongitude() == null
                || userLat == 0.0 || userLon == 0.0) {
            return W_DISTANCE * 0.5;
        }

        double dist = haversine(
                userLat,
                userLon,
                program.getLatitude().doubleValue(),
                program.getLongitude().doubleValue()
        );

        if (dist <= 1.0) return W_DISTANCE;
        if (dist <= 3.0) return W_DISTANCE * 0.8;
        if (dist <= 5.0) return W_DISTANCE * 0.6;
        if (dist <= 10.0) return W_DISTANCE * 0.4;

        return W_DISTANCE * 0.1;
    }

    private double calcBudgetScore(Program program, RecommendationPreference preference) {
        int price = program.getPrice() != null ? program.getPrice() : 0;
        boolean isFree = Boolean.TRUE.equals(program.getIsFree()) || price == 0;

        if (preference == null || preference.getMonthlyBudget() == null) {
            return isFree ? W_BUDGET : W_BUDGET * 0.5;
        }

        return switch (preference.getMonthlyBudget()) {
            case FREE -> isFree ? W_BUDGET : W_BUDGET * 0.2;
            case ZERO_TO_TEN -> price <= 100000 ? W_BUDGET : W_BUDGET * 0.3;
            case TEN_TO_TWENTY -> price <= 200000 ? W_BUDGET : W_BUDGET * 0.3;
            case OVER_TWENTY, ANY -> W_BUDGET;
        };
    }

    private double calcAgeScore(Program program, int childAge) {
        if (program.getTargetAgeMin() == null && program.getTargetAgeMax() == null) {
            return W_AGE * 0.5;
        }

        boolean minOk = program.getTargetAgeMin() == null || program.getTargetAgeMin() <= childAge;
        boolean maxOk = program.getTargetAgeMax() == null || program.getTargetAgeMax() >= childAge;

        return (minOk && maxOk) ? W_AGE : 0.0;
    }

    private double calcKeywordScore(Program program, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return 0.0;
        }

        String searchText = buildProgramSearchText(program);

        List<String> distinctKeywords = keywords.stream()
                .filter(keyword -> keyword != null && !keyword.isBlank())
                .map(String::trim)
                .distinct()
                .toList();

        if (distinctKeywords.isEmpty()) {
            return 0.0;
        }

        long matchCount = distinctKeywords.stream()
                .filter(keyword -> isKeywordMatched(searchText, keyword))
                .count();

        if (matchCount == 0) {
            return 0.0;
        }

        double denominator = Math.min(distinctKeywords.size(), 3);

        return Math.min(W_KEYWORD, ((double) matchCount / denominator) * W_KEYWORD);
    }

    private String buildProgramSearchText(Program program) {
        StringBuilder sb = new StringBuilder();

        appendText(sb, program.getTitle());
        appendText(sb, program.getCategory());
        appendText(sb, program.getClassType());
        appendText(sb, program.getDescription());
        appendText(sb, program.getRegion());

        if (program.getInstitution() != null) {
            appendText(sb, program.getInstitution().getInstitutionName());
        }

        if (program.getTags() != null) {
            program.getTags().forEach(tag -> appendText(sb, tag.getTag()));
        }

        return sb.toString().toLowerCase();
    }

    private void appendText(StringBuilder sb, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(" ").append(value);
        }
    }

    private boolean isKeywordMatched(String searchText, String keyword) {
        String normalizedKeyword = keyword.toLowerCase().trim();

        if (searchText.contains(normalizedKeyword)) {
            return true;
        }

        return switch (normalizedKeyword) {
            case "기초학습 부족" ->
                    containsAny(searchText, "기초", "학습", "교육", "국어", "수학", "영어", "논술");

            case "특정 과목 보완 필요" ->
                    containsAny(searchText, "국어", "수학", "영어", "과학", "논술", "학습", "교육");

            case "맡길 곳 필요" ->
                    containsAny(searchText, "돌봄", "방과후", "센터", "보육", "보호");

            case "친구관계", "친구 관계" ->
                    containsAny(searchText, "사회성", "또래", "협동", "관계", "집단", "놀이", "가족", "체험", "활동");

            case "성격" ->
                    containsAny(searchText, "체험", "놀이", "사회성", "협동", "관계", "가족", "야외", "활동", "생태", "농사", "텃밭");

            case "자신감 부족" ->
                    containsAny(searchText, "발표", "체험", "미술", "음악", "체육", "놀이", "창의");

            case "게임 과몰입" ->
                    containsAny(searchText, "체육", "스포츠", "야외", "활동", "운동", "체험");

            case "비용 부담" ->
                    containsAny(searchText, "무료", "지원", "바우처", "할인", "공공", "복지");

            default -> false;
        };
    }

    private boolean containsAny(String searchText, String... words) {
        for (String word : words) {
            if (searchText.contains(word)) {
                return true;
            }
        }

        return false;
    }

    private double calcClassTypeScore(Program program, RecommendationPreference preference) {
        if (preference == null || preference.getClassType() == null || program.getClassType() == null) {
            return W_CLASS_TYPE * 0.5;
        }

        return preference.getClassType().name().equals(program.getClassType())
                ? W_CLASS_TYPE
                : W_CLASS_TYPE * 0.2;
    }

    private double calcReviewScore(Program program) {
        double rating = program.getRatingAvg() != null
                ? program.getRatingAvg().doubleValue()
                : 0.0;

        return (rating / 5.0) * W_REVIEW;
    }

    private List<ReasonCode> buildReasonCodes(double scoreDistance, double scoreBudget,
                                              double scoreAge, double scoreKeyword,
                                              double scoreClassType, double scoreRecruiting,
                                              double scoreReview) {
        List<ReasonCode> codes = new ArrayList<>();

        if (scoreDistance >= W_DISTANCE * 0.6) codes.add(ReasonCode.DISTANCE_CLOSE);
        if (scoreBudget >= W_BUDGET) codes.add(ReasonCode.BUDGET_FIT);
        if (scoreAge >= W_AGE) codes.add(ReasonCode.AGE_FIT);
        if (scoreKeyword > 0) codes.add(ReasonCode.KEYWORD_MATCH);
        if (scoreClassType >= W_CLASS_TYPE) codes.add(ReasonCode.CLASS_TYPE_MATCH);
        if (scoreRecruiting > 0) codes.add(ReasonCode.RECRUITING_OPEN);
        if (scoreReview >= W_REVIEW * 0.7) codes.add(ReasonCode.HIGH_RATING);

        return codes;
    }

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private BigDecimal bd(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}

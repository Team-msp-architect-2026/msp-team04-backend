package com.moment.momentbackend.recommendation.service;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.recommendation.dto.ScoreBreakdownDto;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ScoringService {

    private static final double W_DISTANCE = 25.0;
    private static final double W_BUDGET = 20.0;
    private static final double W_AGE = 20.0;
    private static final double W_CLASS_TYPE = 15.0;
    private static final double W_RECRUITING = 10.0;
    private static final double W_REVIEW = 10.0;

    public ScoreBreakdownDto calculate(Program program, RecommendationPreference preference,
                                       int childAge, double userLat, double userLon) {

        double scoreDistance = calcDistanceScore(program, userLat, userLon);
        double scoreBudget = calcBudgetScore(program, preference);
        double scoreAge = calcAgeScore(program, childAge);
        double scoreKeyword = 0.0;
        double scoreClassType = calcClassTypeScore(program, preference);
        double scoreRecruiting = program.getIsRecruiting() ? W_RECRUITING : 0.0;
        double scoreReview = calcReviewScore(program);

        double total = scoreDistance + scoreBudget + scoreAge + scoreKeyword
                + scoreClassType + scoreRecruiting + scoreReview;
        total = Math.min(total, 100.0);

        return ScoreBreakdownDto.builder()
                .scoreDistance(bd(scoreDistance))
                .scoreBudget(bd(scoreBudget))
                .scoreAge(bd(scoreAge))
                .scoreKeyword(bd(scoreKeyword))
                .scoreClassType(bd(scoreClassType))
                .scoreRecruiting(bd(scoreRecruiting))
                .scoreReview(bd(scoreReview))
                .totalScore(bd(total))
                .build();
    }

    private double calcDistanceScore(Program program, double userLat, double userLon) {
        if (program.getLatitude() == null || program.getLongitude() == null
                || userLat == 0.0 || userLon == 0.0) {
            return W_DISTANCE * 0.5;
        }
        double dist = haversine(userLat, userLon,
                program.getLatitude().doubleValue(),
                program.getLongitude().doubleValue());

        if (dist <= 1.0) return W_DISTANCE;
        if (dist <= 3.0) return W_DISTANCE * 0.8;
        if (dist <= 5.0) return W_DISTANCE * 0.6;
        if (dist <= 10.0) return W_DISTANCE * 0.4;
        return W_DISTANCE * 0.1;
    }

    private double calcBudgetScore(Program program, RecommendationPreference preference) {
        if (preference.getMonthlyBudget() == null) return W_BUDGET * 0.5;

        int price = program.getPrice();
        return switch (preference.getMonthlyBudget()) {
            case UNDER_10 -> price <= 100000 ? W_BUDGET : W_BUDGET * 0.3;
            case UNDER_30 -> price <= 300000 ? W_BUDGET : W_BUDGET * 0.3;
            case UNDER_50 -> price <= 500000 ? W_BUDGET : W_BUDGET * 0.3;
            case OVER_50 -> W_BUDGET;
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

    private double calcClassTypeScore(Program program, RecommendationPreference preference) {
        if (preference.getClassType() == null || program.getClassType() == null) {
            return W_CLASS_TYPE * 0.5;
        }
        // enum → String 변환 후 비교
        return preference.getClassType().name().equals(program.getClassType())
                ? W_CLASS_TYPE
                : W_CLASS_TYPE * 0.2;
    }

    private double calcReviewScore(Program program) {
        double rating = program.getRatingAvg() != null ? program.getRatingAvg().doubleValue() : 0.0;
        return (rating / 5.0) * W_REVIEW;
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
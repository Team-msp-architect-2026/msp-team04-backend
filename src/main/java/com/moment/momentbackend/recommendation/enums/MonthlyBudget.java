package com.moment.momentbackend.recommendation.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum MonthlyBudget {
    FREE("FREE"),          // 무료
    ZERO_TO_TEN("0-10"),   // 0~10만원
    TEN_TO_TWENTY("10-20"),// 10~20만원
    OVER_TWENTY("20+"),    // 20만원+
    ANY("ANY");            // 무관

    private final String code;

    MonthlyBudget(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static MonthlyBudget fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown MonthlyBudget code: " + code));
    }
}

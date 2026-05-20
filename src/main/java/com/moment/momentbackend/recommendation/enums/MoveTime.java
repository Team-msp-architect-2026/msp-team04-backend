package com.moment.momentbackend.recommendation.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum MoveTime {
    UNDER_TEN("10MIN"),          // 10분 이내
    TEN_TO_TWENTY("10-20MIN"),   // 10~20분
    OVER_TWENTY("20MIN+"),       // 20분+
    ANY("ANY");                  // 무관

    private final String code;

    MoveTime(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static MoveTime fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown MoveTime code: " + code));
    }
}

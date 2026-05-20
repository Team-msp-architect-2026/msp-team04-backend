package com.moment.momentbackend.recommendation.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OnlinePreference {
    ONLINE_OK("ONLINE_OK"),       // 온라인 수업도 가능
    OFFLINE_ONLY("OFFLINE_ONLY"), // 오프라인만
    ANY("ANY");                   // 상관없음

    private final String code;

    OnlinePreference(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static OnlinePreference fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OnlinePreference code: " + code));
    }
}

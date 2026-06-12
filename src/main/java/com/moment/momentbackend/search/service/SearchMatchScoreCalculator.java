package com.moment.momentbackend.search.service;

import com.moment.momentbackend.program.entity.Program;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

final class SearchMatchScoreCalculator {

    private SearchMatchScoreCalculator() {
    }

    static int calculate(Program program, String keyword) {
        if (program == null || keyword == null || keyword.isBlank()) {
            return 0;
        }

        String normalized = normalizeKeyword(keyword);
        if (normalized.isBlank()) {
            normalized = normalizeText(keyword);
        }
        final String normalizedKeyword = normalized;

        List<String> tokens = Arrays.stream(normalizedKeyword.split(" "))
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .distinct()
                .toList();

        if (tokens.isEmpty()) {
            return 0;
        }

        String title = normalizeText(program.getTitle());
        String institutionName = program.getInstitution() != null
                ? normalizeText(program.getInstitution().getInstitutionName())
                : "";
        String institutionAddress = program.getInstitution() != null
                ? normalizeText(program.getInstitution().getAddress())
                : "";
        String category = normalizeText(program.getCategory());
        String region = normalizeText(program.getRegion());
        String detailAddress = normalizeText(program.getDetailAddress());
        String description = normalizeText(program.getDescription());
        List<String> tags = program.getTags() == null
                ? List.of()
                : program.getTags().stream()
                        .map(tag -> normalizeText(tag.getTag()))
                        .toList();

        if (contains(title, normalizedKeyword)) {
            return 97;
        }

        if (containsAllTokens(title, tokens)) {
            return 95;
        }

        if (contains(institutionName, normalizedKeyword)) {
            return 92;
        }

        if (containsAllTokens(institutionName, tokens)) {
            return 90;
        }

        if (tags.stream().anyMatch(tag -> contains(tag, normalizedKeyword))) {
            return 88;
        }

        if (tags.stream().anyMatch(tag -> containsAllTokens(tag, tokens))) {
            return 86;
        }

        if (contains(category, normalizedKeyword) || containsAllTokens(category, tokens)) {
            return 78;
        }

        if (contains(region, normalizedKeyword)
                || contains(detailAddress, normalizedKeyword)
                || contains(institutionAddress, normalizedKeyword)
                || containsAllTokens(region, tokens)
                || containsAllTokens(detailAddress, tokens)
                || containsAllTokens(institutionAddress, tokens)) {
            return 72;
        }

        if (contains(description, normalizedKeyword)) {
            return 55;
        }

        if (containsAllTokens(description, tokens)) {
            return 45;
        }

        if (containsAnyToken(title, tokens)
                || containsAnyToken(institutionName, tokens)
                || containsAnyToken(category, tokens)
                || containsAnyToken(region, tokens)
                || containsAnyToken(detailAddress, tokens)
                || containsAnyToken(institutionAddress, tokens)
                || tags.stream().anyMatch(tag -> containsAnyToken(tag, tokens))) {
            return 35;
        }

        if (containsAnyToken(description, tokens)) {
            return 25;
        }

        return 0;
    }

    private static boolean contains(String value, String keyword) {
        return value != null && keyword != null && !keyword.isBlank() && value.contains(keyword);
    }

    private static boolean containsAllTokens(String value, List<String> tokens) {
        return value != null && !value.isBlank() && tokens.stream().allMatch(value::contains);
    }

    private static boolean containsAnyToken(String value, List<String> tokens) {
        return value != null && !value.isBlank() && tokens.stream().anyMatch(value::contains);
    }

    private static String normalizeText(String value) {
        return value == null
                ? ""
                : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private static String normalizeKeyword(String keyword) {
        String normalized = normalizeText(keyword);

        normalized = removeKeyword(normalized, "찾아줘");
        normalized = removeKeyword(normalized, "추천해줘");
        normalized = removeKeyword(normalized, "알려줘");
        normalized = removeKeyword(normalized, "보여줘");
        normalized = removeKeyword(normalized, "검색해줘");
        normalized = removeKeyword(normalized, "갈 수 있는");
        normalized = removeKeyword(normalized, "들을 수 있는");
        normalized = removeKeyword(normalized, "참여할 수 있는");
        normalized = removeKeyword(normalized, "할 수 있는");
        normalized = removeKeyword(normalized, "하는");
        normalized = removeKeyword(normalized, "에서");
        normalized = removeKeyword(normalized, "으로");
        normalized = removeKeyword(normalized, "아이랑");
        normalized = removeKeyword(normalized, "아이와");
        normalized = removeKeyword(normalized, "아이가");
        normalized = removeKeyword(normalized, "아이");
        normalized = removeKeyword(normalized, "어린이");
        normalized = removeKeyword(normalized, "무료로");
        normalized = removeKeyword(normalized, "무료");
        normalized = removeKeyword(normalized, "무상");
        normalized = removeKeyword(normalized, "주말에");
        normalized = removeKeyword(normalized, "주말");
        normalized = removeKeyword(normalized, "토요일");
        normalized = removeKeyword(normalized, "일요일");
        normalized = removeKeyword(normalized, "토/일");
        normalized = removeKeyword(normalized, "토일");
        normalized = removeKeyword(normalized, "돌봄");
        normalized = removeKeyword(normalized, "케어");
        normalized = removeKeyword(normalized, "공공기관");
        normalized = removeKeyword(normalized, "공공시설");
        normalized = removeKeyword(normalized, "공공");
        normalized = removeKeyword(normalized, "기관");
        normalized = removeKeyword(normalized, "소규모");

        normalized = normalized.replaceAll("만\\s*\\d+\\s*세", " ")
                .replaceAll("\\d+\\s*세", " ")
                .replaceAll("\\d+\\s*살", " ");

        normalized = removeKeyword(normalized, "프로그램");
        normalized = removeKeyword(normalized, "수업");
        normalized = removeKeyword(normalized, "강의");
        normalized = removeKeyword(normalized, "클래스");

        return normalized.trim().replaceAll("\\s+", " ");
    }

    private static String removeKeyword(String value, String keyword) {
        return value.replace(keyword, " ");
    }
}

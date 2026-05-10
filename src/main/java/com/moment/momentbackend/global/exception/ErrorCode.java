package com.moment.momentbackend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400
    INVALID_PARAM(HttpStatus.BAD_REQUEST, "잘못된 요청 파라미터"),
    INVALID_AGREEMENT(HttpStatus.BAD_REQUEST, "필수 약관 동의가 필요합니다"),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 토큰 없음 또는 만료"),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한 없음"),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스 없음"),
    CHILD_NOT_FOUND(HttpStatus.NOT_FOUND, "자녀 정보를 찾을 수 없습니다"),
    PROGRAM_NOT_FOUND(HttpStatus.NOT_FOUND, "프로그램 정보를 찾을 수 없습니다"),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "신청 정보를 찾을 수 없습니다"),

    // 409
    ALREADY_APPLIED(HttpStatus.CONFLICT, "이미 신청한 프로그램입니다"),
    APPLICATION_LOCK_FAILED(HttpStatus.CONFLICT, "신청 처리 중입니다. 잠시 후 다시 시도해주세요"),
    APPLICATION_NOT_AVAILABLE(HttpStatus.CONFLICT, "현재 신청할 수 없는 프로그램입니다"),

    // 410
    SOLD_OUT(HttpStatus.GONE, "선착순 마감"),

    // 500
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");

    private final HttpStatus status;
    private final String message;
}
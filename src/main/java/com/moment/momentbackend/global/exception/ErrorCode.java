package com.moment.momentbackend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400
    INVALID_PARAM(HttpStatus.BAD_REQUEST, "잘못된 요청 파라미터"),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 토큰 없음 또는 만료"),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한 없음"),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스 없음"),

    // 409
    ALREADY_APPLIED(HttpStatus.CONFLICT, "이미 예약한 기관"),

    // 410
    SOLD_OUT(HttpStatus.GONE, "선착순 마감"),

    // 500
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");

    private final HttpStatus status;
    private final String message;
}
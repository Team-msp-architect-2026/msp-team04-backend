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
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다"),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "허용되지 않는 값입니다"),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 토큰 없음 또는 만료"),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한 없음"),
    CHILD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인의 자녀 프로필이 아닙니다"),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스 없음"),
    CHILD_NOT_FOUND(HttpStatus.NOT_FOUND, "자녀 정보를 찾을 수 없습니다"),
    PROGRAM_NOT_FOUND(HttpStatus.NOT_FOUND, "프로그램 정보를 찾을 수 없습니다"),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "신청 정보를 찾을 수 없습니다"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다"),
    PREFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "선호도 정보를 찾을 수 없습니다"),

    // 409
    ALREADY_APPLIED(HttpStatus.CONFLICT, "이미 신청한 프로그램입니다"),
    APPLICATION_LOCK_FAILED(HttpStatus.CONFLICT, "신청 처리 중입니다. 잠시 후 다시 시도해주세요"),
    APPLICATION_NOT_AVAILABLE(HttpStatus.CONFLICT, "현재 신청할 수 없는 프로그램입니다"),
    INVALID_APPLICATION_STATUS(HttpStatus.CONFLICT, "현재 신청 상태에서는 처리할 수 없습니다"),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 결제입니다"),

    // 410
    SOLD_OUT(HttpStatus.GONE, "선착순 마감"),
    PAYMENT_EXPIRED(HttpStatus.GONE, "결제 유효 시간이 만료되었습니다"),

    // 500
    PAYMENT_CONFIRM_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토스페이먼츠 결제 승인 처리에 실패했습니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");

    private final HttpStatus status;
    private final String message;
}
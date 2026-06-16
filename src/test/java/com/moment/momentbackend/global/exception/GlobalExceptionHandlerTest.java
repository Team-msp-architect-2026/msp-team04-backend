package com.moment.momentbackend.global.exception;

import com.moment.momentbackend.global.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("정적 리소스 또는 미등록 경로 요청은 500이 아니라 404로 응답한다")
    void noResourceFoundExceptionReturnsNotFound() {
        ResponseEntity<ApiResponse<?>> response =
                handler.handleNotFoundException(new NoResourceFoundException(HttpMethod.GET, "/not-exist"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("핸들러가 없는 요청은 500이 아니라 404로 응답한다")
    void noHandlerFoundExceptionReturnsNotFound() {
        ResponseEntity<ApiResponse<?>> response =
                handler.handleNotFoundException(new NoHandlerFoundException("GET", "/not-exist", new HttpHeaders()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
    }
}

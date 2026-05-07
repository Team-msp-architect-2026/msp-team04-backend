package com.moment.momentbackend.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());

        String auth = request.getHeader("Authorization");
        String maskedAuth = (auth != null) ? "Bearer [MASKED]" : "없음";

        log.info("[요청] {} {} | Authorization: {}",
                request.getMethod(),
                request.getRequestURI(),
                maskedAuth);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        log.info("[응답] {} {} | status: {} | 소요시간: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);
    }
}
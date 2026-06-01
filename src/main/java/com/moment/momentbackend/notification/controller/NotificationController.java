
package com.moment.momentbackend.notification.controller;

import com.moment.momentbackend.global.response.ApiResponse;
import com.moment.momentbackend.notification.dto.NotificationResponse;
import com.moment.momentbackend.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록 조회")
    public ApiResponse<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.ok(notificationService.getNotifications(userId));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리")
    public ApiResponse<NotificationResponse> markAsRead(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId
    ) {
        return ApiResponse.ok(
                notificationService.markAsRead(userId, notificationId),
                "알림 읽음 처리가 완료되었습니다."
        );
    }
}

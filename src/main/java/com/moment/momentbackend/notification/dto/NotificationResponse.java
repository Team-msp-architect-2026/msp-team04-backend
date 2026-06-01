
package com.moment.momentbackend.notification.dto;

import com.moment.momentbackend.notification.entity.Notification;
import com.moment.momentbackend.notification.type.NotificationReferenceType;
import com.moment.momentbackend.notification.type.NotificationType;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String title;
    private String body;
    private Boolean isRead;
    private String createdAt;
    private Long referenceId;
    private NotificationReferenceType referenceType;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .body(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt() == null ? null : notification.getCreatedAt().toString())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .build();
    }
}

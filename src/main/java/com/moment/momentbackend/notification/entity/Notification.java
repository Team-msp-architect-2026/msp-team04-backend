
package com.moment.momentbackend.notification.entity;

import com.moment.momentbackend.notification.type.NotificationReferenceType;
import com.moment.momentbackend.notification.type.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "reference_id")
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 50)
    private NotificationReferenceType referenceType;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Notification(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Long referenceId,
            NotificationReferenceType referenceType
    ) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public static Notification create(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Long referenceId,
            NotificationReferenceType referenceType
    ) {
        return new Notification(userId, type, title, message, referenceId, referenceType);
    }

    public void markAsRead() {
        this.isRead = true;
    }
}


package com.moment.momentbackend.notification.repository;

import com.moment.momentbackend.notification.entity.Notification;
import com.moment.momentbackend.notification.type.NotificationReferenceType;
import com.moment.momentbackend.notification.type.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndTypeAndReferenceTypeAndReferenceId(
            Long userId,
            NotificationType type,
            NotificationReferenceType referenceType,
            Long referenceId
    );
}

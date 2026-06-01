
package com.moment.momentbackend.notification.service;

import com.moment.momentbackend.notification.entity.Notification;

public interface NotificationPublisher {

    void publish(Notification notification);
}

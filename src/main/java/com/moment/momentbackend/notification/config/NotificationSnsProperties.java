
package com.moment.momentbackend.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "notification.sns")
public class NotificationSnsProperties {

    private boolean enabled = false;
    private String topicArn = "";
}

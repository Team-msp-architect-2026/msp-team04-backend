package com.moment.momentbackend.upload.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "upload.s3.profile")
public class ProfileImageUploadProperties {

    private String bucketName;
    private String publicUrlBase;
    private long presignedUrlExpirationSeconds = 600L;
    private long maxFileSizeBytes = 5L * 1024L * 1024L;
    private String keyPrefix = "uploads/profile";
}

package com.moment.momentbackend.upload;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.upload.config.ProfileImageUploadProperties;
import com.moment.momentbackend.upload.service.ProfileImageUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ProfileImageUploadServiceTest {

    private ProfileImageUploadProperties properties;
    private ProfileImageUploadService profileImageUploadService;

    @BeforeEach
    void setUp() {
        properties = new ProfileImageUploadProperties();
        properties.setBucketName("test-profile-image-bucket");
        properties.setPublicUrlBase("https://cdn.example.com");
        properties.setPresignedUrlExpirationSeconds(600L);
        properties.setMaxFileSizeBytes(5L * 1024L * 1024L);
        properties.setKeyPrefix("uploads/profile");

        profileImageUploadService = new ProfileImageUploadService(
                mock(S3Presigner.class),
                properties
        );
    }

    @Test
    void unsupportedContentTypeThrowsException() {
        assertThatThrownBy(() -> profileImageUploadService.createPresignedUrl(1L, "image/gif"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_UPLOAD_CONTENT_TYPE);
    }

    @Test
    void missingBucketConfigThrowsException() {
        properties.setBucketName("");

        assertThatThrownBy(() -> profileImageUploadService.createPresignedUrl(1L, "image/jpeg"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UPLOAD_STORAGE_CONFIG_MISSING);
    }

    @Test
    void missingUserIdThrowsUnauthorizedException() {
        assertThatThrownBy(() -> profileImageUploadService.createPresignedUrl(null, "image/jpeg"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHORIZED);
    }
}

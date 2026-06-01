package com.moment.momentbackend.upload.service;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.upload.config.ProfileImageUploadProperties;
import com.moment.momentbackend.upload.dto.ProfileImagePresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileImageUploadService {

    private static final Map<String, String> ALLOWED_CONTENT_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png"
    );

    private final S3Presigner s3Presigner;
    private final ProfileImageUploadProperties properties;

    public ProfileImagePresignedUrlResponse createPresignedUrl(Long userId, String contentType) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String normalizedContentType = normalizeContentType(contentType);
        String extension = ALLOWED_CONTENT_TYPES.get(normalizedContentType);

        if (extension == null) {
            throw new CustomException(ErrorCode.INVALID_UPLOAD_CONTENT_TYPE);
        }

        validateUploadConfig();

        String objectKey = createObjectKey(userId, extension);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(objectKey)
                .contentType(normalizedContentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(properties.getPresignedUrlExpirationSeconds()))
                .putObjectRequest(putObjectRequest)
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            return ProfileImagePresignedUrlResponse.builder()
                    .uploadUrl(presignedRequest.url().toString())
                    .fileUrl(createFileUrl(objectKey))
                    .objectKey(objectKey)
                    .method("PUT")
                    .contentType(normalizedContentType)
                    .expiresInSeconds(properties.getPresignedUrlExpirationSeconds())
                    .maxFileSizeBytes(properties.getMaxFileSizeBytes())
                    .build();
        } catch (RuntimeException e) {
            throw new CustomException(ErrorCode.UPLOAD_PRESIGN_FAILED);
        }
    }

    private String normalizeContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            throw new CustomException(ErrorCode.INVALID_UPLOAD_CONTENT_TYPE);
        }

        return contentType.trim().toLowerCase(Locale.ROOT);
    }

    private void validateUploadConfig() {
        if (!StringUtils.hasText(properties.getBucketName())
                || !StringUtils.hasText(properties.getPublicUrlBase())) {
            throw new CustomException(ErrorCode.UPLOAD_STORAGE_CONFIG_MISSING);
        }

        if (properties.getPresignedUrlExpirationSeconds() <= 0
                || properties.getMaxFileSizeBytes() <= 0) {
            throw new CustomException(ErrorCode.UPLOAD_STORAGE_CONFIG_MISSING);
        }
    }

    private String createObjectKey(Long userId, String extension) {
        LocalDate now = LocalDate.now();
        String prefix = normalizePrefix(properties.getKeyPrefix());

        return "%s/users/%d/%04d/%02d/%s.%s".formatted(
                prefix,
                userId,
                now.getYear(),
                now.getMonthValue(),
                UUID.randomUUID(),
                extension
        );
    }

    private String normalizePrefix(String prefix) {
        if (!StringUtils.hasText(prefix)) {
            return "uploads/profile";
        }

        return prefix
                .trim()
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");
    }

    private String createFileUrl(String objectKey) {
        return properties.getPublicUrlBase().replaceAll("/+$", "") + "/" + objectKey;
    }
}

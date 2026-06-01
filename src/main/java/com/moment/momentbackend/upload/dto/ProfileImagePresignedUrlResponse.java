package com.moment.momentbackend.upload.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileImagePresignedUrlResponse {

    private String uploadUrl;
    private String fileUrl;
    private String objectKey;
    private String method;
    private String contentType;
    private long expiresInSeconds;
    private long maxFileSizeBytes;
}

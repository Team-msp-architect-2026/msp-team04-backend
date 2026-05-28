package com.moment.momentbackend.batch.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3RawReader {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    public JsonNode read(String bucketName, String objectKey) {
        log.info("S3 읽기 시작: {}/{}", bucketName, objectKey);
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request)) {
            byte[] bytes = response.readAllBytes();
            log.info("S3 읽은 bytes 앞부분: {}", new String(bytes, 0, Math.min(100, bytes.length)));
            ObjectMapper jsonMapper = new ObjectMapper();
            return jsonMapper.readTree(bytes);
        } catch (Exception e) {
            log.error("S3 읽기 실패: {}/{} - {}", bucketName, objectKey, e.getMessage());
            throw new RuntimeException("S3 Raw 읽기 실패", e);
        }
    }
}
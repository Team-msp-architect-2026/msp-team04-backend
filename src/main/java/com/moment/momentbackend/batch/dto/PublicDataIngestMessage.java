package com.moment.momentbackend.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicDataIngestMessage {
    private String sourceName;
    private String sourceDetail;
    private String rawBucketName;
    private String rawObjectKey;
    private String environment;
    private Integer recordCount;
    private String collectedAt;
}
package com.moment.momentbackend.embedding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmbeddingResponseDto {

    private Long sourceId;
    private String sourceType;
    private float[] vector;
    private boolean success;
}
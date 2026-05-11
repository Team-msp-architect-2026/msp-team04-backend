package com.moment.momentbackend.embedding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmbeddingRequestDto {

    private Long sourceId;       // program_id 또는 review_id
    private String sourceType;   // "PROGRAM" 또는 "REVIEW"
    private String text;         // 임베딩할 정규화된 텍스트
}
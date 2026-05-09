package com.moment.momentbackend.batch.processor;

import com.moment.momentbackend.batch.dto.InstitutionCsvDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InstitutionItemProcessor implements ItemProcessor<InstitutionCsvDto, InstitutionCsvDto> {

    @Override
    public InstitutionCsvDto process(InstitutionCsvDto dto) {
        // external_source / external_id 둘 중 하나만 있으면 실패 처리
        if (dto.getExternalSource() == null || dto.getExternalSource().isBlank() ||
                dto.getExternalId() == null || dto.getExternalId().isBlank()) {
            log.error("institution 데이터 유효성 실패 - externalSource: {}, externalId: {}",
                    dto.getExternalSource(), dto.getExternalId());
            return null;
        }

        // 기관명 정규화
        if (dto.getInstitutionName() != null) {
            dto.setInstitutionName(dto.getInstitutionName().trim());
        }

        // 주소 정규화
        if (dto.getAddress() != null) {
            dto.setAddress(dto.getAddress().trim());
        }

        // 지역 정규화
        if (dto.getRegion() != null) {
            dto.setRegion(dto.getRegion().trim());
        }

        return dto;
    }
}
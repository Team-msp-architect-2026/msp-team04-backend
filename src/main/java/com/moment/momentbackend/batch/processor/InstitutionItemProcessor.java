package com.moment.momentbackend.batch.processor;

import com.moment.momentbackend.batch.dto.InstitutionCsvDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class InstitutionItemProcessor implements ItemProcessor<InstitutionCsvDto, InstitutionCsvDto> {

    @Override
    public InstitutionCsvDto process(InstitutionCsvDto dto) {
        boolean hasSource = dto.getExternalSource() != null && !dto.getExternalSource().isBlank();
        boolean hasId = dto.getExternalId() != null && !dto.getExternalId().isBlank();

        if (!hasSource && !hasId) {
            // 수기 데이터 정책: 둘 다 없으면 MANUAL + UUID 부여
            dto.setExternalSource("MANUAL");
            dto.setExternalId(UUID.randomUUID().toString());
            log.info("수기 데이터 식별자 부여 - institutionName: {}, externalId: {}", dto.getInstitutionName(), dto.getExternalId());
        } else if (hasSource != hasId) {
            // 하나만 있으면 검증 실패
            log.error("기관 데이터 유효성 실패 - externalSource: {}, externalId: {}",
                    dto.getExternalSource(), dto.getExternalId());
            return null;
        }

        if (dto.getInstitutionName() != null) dto.setInstitutionName(dto.getInstitutionName().trim());
        if (dto.getAddress() != null) dto.setAddress(dto.getAddress().trim());
        if (dto.getRegion() != null) dto.setRegion(dto.getRegion().trim());

        return dto;
    }
}
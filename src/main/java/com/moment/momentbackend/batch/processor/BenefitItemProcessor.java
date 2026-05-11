package com.moment.momentbackend.batch.processor;

import com.moment.momentbackend.batch.dto.BenefitCsvDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class BenefitItemProcessor implements ItemProcessor<BenefitCsvDto, BenefitCsvDto> {

    @Override
    public BenefitCsvDto process(BenefitCsvDto item) {
        boolean hasSource = item.getExternalSource() != null && !item.getExternalSource().isBlank();
        boolean hasId = item.getExternalId() != null && !item.getExternalId().isBlank();

        if (!hasSource && !hasId) {
            // 수기 데이터 정책: 둘 다 없으면 MANUAL + UUID 부여
            item.setExternalSource("MANUAL");
            item.setExternalId(UUID.randomUUID().toString());
            log.info("수기 데이터 식별자 부여 - benefitName: {}, externalId: {}", item.getBenefitName(), item.getExternalId());
        } else if (hasSource != hasId) {
            // 하나만 있으면 검증 실패
            log.error("지원금 데이터 유효성 실패 - externalSource: {}, externalId: {}",
                    item.getExternalSource(), item.getExternalId());
            return null;
        }

        if (item.getBenefitName() == null) {
            log.warn("필수 필드 누락 - 스킵: externalSource: {}, externalId: {}",
                    item.getExternalSource(), item.getExternalId());
            return null;
        }

        return item;
    }
}
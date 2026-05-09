package com.moment.momentbackend.batch.processor;

import com.moment.momentbackend.batch.dto.BenefitCsvDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BenefitItemProcessor implements ItemProcessor<BenefitCsvDto, BenefitCsvDto> {

    @Override
    public BenefitCsvDto process(BenefitCsvDto item) {
        if (item.getExternalSource() == null || item.getExternalId() == null) {
            log.warn("externalSource 또는 externalId 누락 - 스킵: {}", item.getBenefitName());
            return null;
        }
        if (item.getBenefitName() == null) {
            log.warn("benefitName 누락 - 스킵: {}", item.getExternalId());
            return null;
        }
        return item;
    }
}
package com.moment.momentbackend.batch.processor;

import com.moment.momentbackend.batch.dto.ProgramCsvDto;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramItemProcessor implements ItemProcessor<ProgramCsvDto, ProgramCsvDto> {

    @Override
    public ProgramCsvDto process(ProgramCsvDto item) {
        // externalSource, externalId 둘 다 있어야 함
        if (item.getExternalSource() == null || item.getExternalId() == null) {
            log.warn("externalSource 또는 externalId 누락 - 스킵: {}", item.getTitle());
            return null;
        }
        if (item.getTitle() == null || item.getCategory() == null) {
            log.warn("필수 필드 누락 - 스킵: {}", item.getExternalId());
            return null;
        }
        return item;
    }
}
package com.moment.momentbackend.batch.writer;

import com.moment.momentbackend.batch.dto.BenefitCsvDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BenefitItemWriter implements ItemWriter<BenefitCsvDto> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends BenefitCsvDto> chunk) {
        for (BenefitCsvDto dto : chunk.getItems()) {
            try {
                upsert(dto);
            } catch (Exception e) {
                log.error("지원금 upsert 실패 - externalSource: {}, externalId: {}, 오류: {}",
                        dto.getExternalSource(), dto.getExternalId(), e.getMessage());
            }
        }
    }

    private void upsert(BenefitCsvDto dto) {
        String sql = """
                INSERT INTO benefit_master (
                    benefit_name, benefit_type, support_amount, support_description,
                    apply_link, min_age, max_age, region, is_active,
                    external_source, external_id, last_synced_at, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (external_source, external_id) DO UPDATE SET
                    benefit_name = EXCLUDED.benefit_name,
                    benefit_type = EXCLUDED.benefit_type,
                    support_amount = EXCLUDED.support_amount,
                    support_description = EXCLUDED.support_description,
                    apply_link = EXCLUDED.apply_link,
                    min_age = EXCLUDED.min_age,
                    max_age = EXCLUDED.max_age,
                    region = EXCLUDED.region,
                    is_active = EXCLUDED.is_active,
                    last_synced_at = EXCLUDED.last_synced_at
                """;

        jdbcTemplate.update(sql,
                dto.getBenefitName(), dto.getBenefitType(),
                dto.getSupportAmount(), dto.getSupportDescription(),
                dto.getApplyLink(), dto.getMinAge(), dto.getMaxAge(),
                dto.getRegion(),
                dto.getIsActive() != null ? dto.getIsActive() : true,
                dto.getExternalSource(), dto.getExternalId(),
                LocalDateTime.now(), LocalDateTime.now()
        );
        log.info("지원금 upsert 완료: {}", dto.getExternalId());
    }
}
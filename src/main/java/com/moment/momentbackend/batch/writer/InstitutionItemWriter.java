package com.moment.momentbackend.batch.writer;

import com.moment.momentbackend.batch.dto.InstitutionCsvDto;
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
public class InstitutionItemWriter implements ItemWriter<InstitutionCsvDto> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends InstitutionCsvDto> chunk) {
        for (InstitutionCsvDto dto : chunk.getItems()) {
            try {
                upsert(dto);
            } catch (Exception e) {
                log.error("기관 upsert 실패 - externalSource: {}, externalId: {}, 오류: {}",
                        dto.getExternalSource(), dto.getExternalId(), e.getMessage());
            }
        }
    }

    private void upsert(InstitutionCsvDto dto) {
        String sql = """
                INSERT INTO institution (
                    institution_name, address, region, phone, website,
                    institution_type, external_source, external_id, last_synced_at, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (external_source, external_id) DO UPDATE SET
                    institution_name = EXCLUDED.institution_name,
                    address = EXCLUDED.address,
                    region = EXCLUDED.region,
                    phone = EXCLUDED.phone,
                    website = EXCLUDED.website,
                    institution_type = EXCLUDED.institution_type,
                    last_synced_at = EXCLUDED.last_synced_at
                """;

        jdbcTemplate.update(sql,
                dto.getInstitutionName(), dto.getAddress(), dto.getRegion(),
                dto.getPhone(), dto.getWebsite(), dto.getInstitutionType(),
                dto.getExternalSource(), dto.getExternalId(),
                LocalDateTime.now(), LocalDateTime.now()
        );
        log.info("기관 upsert 완료: {}", dto.getExternalId());
    }
}
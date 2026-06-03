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
    public void write(Chunk<? extends InstitutionCsvDto> chunk) throws Exception {
        for (InstitutionCsvDto dto : chunk.getItems()) {
            try {
                upsert(dto);
            } catch (Exception e) {
                log.error("기관 upsert 실패 - externalSource: {}, externalId: {}",
                        dto.getExternalSource(), dto.getExternalId(), e);
                throw e;
            }
        }
    }

    private void upsert(InstitutionCsvDto dto) {
        String sql = """
                INSERT INTO institution (
                    institution_name, address, phone, homepage_url,
                    institution_type, external_source, external_id,
                    last_synced_at, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (external_source, external_id) DO UPDATE SET
                    institution_name = EXCLUDED.institution_name,
                    address = EXCLUDED.address,
                    phone = EXCLUDED.phone,
                    homepage_url = EXCLUDED.homepage_url,
                    institution_type = EXCLUDED.institution_type,
                    last_synced_at = EXCLUDED.last_synced_at,
                    updated_at = EXCLUDED.updated_at
                """;

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(sql,
                dto.getInstitutionName(),
                dto.getAddress(),
                dto.getPhone(),
                dto.getWebsite(),
                dto.getInstitutionType(),
                dto.getExternalSource(),
                dto.getExternalId(),
                now,
                now,
                now
        );

        log.info("기관 upsert 완료: {}", dto.getExternalId());
    }
}

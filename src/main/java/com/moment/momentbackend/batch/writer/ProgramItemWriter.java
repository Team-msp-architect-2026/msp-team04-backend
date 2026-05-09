package com.moment.momentbackend.batch.writer;

import com.moment.momentbackend.batch.dto.ProgramCsvDto;
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
public class ProgramItemWriter implements ItemWriter<ProgramCsvDto> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends ProgramCsvDto> chunk) {
        for (ProgramCsvDto dto : chunk.getItems()) {
            try {
                upsert(dto);
            } catch (Exception e) {
                log.error("프로그램 upsert 실패 - externalId: {}, 오류: {}",
                        dto.getExternalId(), e.getMessage());
            }
        }
    }

    private void upsert(ProgramCsvDto dto) {
        String sql = """
                INSERT INTO program (
                    title, category, description, program_type,
                    target_age_min, target_age_max, price, is_free,
                    region, detail_address, latitude, longitude,
                    class_type, is_recruiting, max_capacity, remain_capacity,
                    is_public, rating_avg, review_count,
                    external_source, external_id, last_synced_at, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, 0, 0, ?, ?, ?, ?)
                ON CONFLICT (external_source, external_id) DO UPDATE SET
                    title = EXCLUDED.title,
                    category = EXCLUDED.category,
                    description = EXCLUDED.description,
                    program_type = EXCLUDED.program_type,
                    target_age_min = EXCLUDED.target_age_min,
                    target_age_max = EXCLUDED.target_age_max,
                    price = EXCLUDED.price,
                    is_free = EXCLUDED.is_free,
                    region = EXCLUDED.region,
                    detail_address = EXCLUDED.detail_address,
                    class_type = EXCLUDED.class_type,
                    is_recruiting = EXCLUDED.is_recruiting,
                    max_capacity = EXCLUDED.max_capacity,
                    remain_capacity = EXCLUDED.remain_capacity,
                    last_synced_at = EXCLUDED.last_synced_at
                """;

        jdbcTemplate.update(sql,
                dto.getTitle(), dto.getCategory(), dto.getDescription(), dto.getProgramType(),
                dto.getTargetAgeMin(), dto.getTargetAgeMax(),
                dto.getPrice() != null ? dto.getPrice() : 0,
                dto.getIsFree() != null ? dto.getIsFree() : false,
                dto.getRegion(), dto.getDetailAddress(), dto.getLatitude(), dto.getLongitude(),
                dto.getClassType(),
                dto.getIsRecruiting() != null ? dto.getIsRecruiting() : true,
                dto.getMaxCapacity(), dto.getRemainCapacity(),
                dto.getExternalSource(), dto.getExternalId(),
                LocalDateTime.now(), LocalDateTime.now()
        );
        log.info("프로그램 upsert 완료: {}", dto.getExternalId());
    }
}
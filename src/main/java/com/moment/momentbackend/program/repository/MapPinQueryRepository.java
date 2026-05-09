package com.moment.momentbackend.program.repository;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.entity.QProgram;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MapPinQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Program> findMapPins(String region) {
        QProgram program = QProgram.program;
        BooleanBuilder builder = new BooleanBuilder();

        // 공개 + 좌표 있는 것만
        builder.and(program.isPublic.isTrue());
        builder.and(program.latitude.isNotNull());
        builder.and(program.longitude.isNotNull());

        // 지역 필터
        if (region != null && !region.isBlank()) {
            builder.and(program.region.containsIgnoreCase(region));
        }

        return queryFactory
                .select(program)
                .from(program)
                .where(builder)
                .fetch();
    }
}
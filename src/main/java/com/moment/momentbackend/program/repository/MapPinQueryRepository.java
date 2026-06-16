package com.moment.momentbackend.program.repository;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.entity.QProgram;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
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

    /**
     * 내 주변 찾기: 위경도 기준 반경 내 프로그램 조회 (Haversine, km)
     */
    public List<Program> findNearbyPins(double lat, double lng, double radiusKm, Integer limit) {
        QProgram program = QProgram.program;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(program.isPublic.isTrue());
        builder.and(program.latitude.isNotNull());
        builder.and(program.longitude.isNotNull());

        // Haversine 거리 (km) 계산식
        NumberExpression<Double> distance = Expressions.numberTemplate(Double.class,
                "(6371 * acos(least(1.0, " +
                        "cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) " +
                        "+ sin(radians({0})) * sin(radians({1})))))",
                lat, program.latitude, program.longitude, lng);

        builder.and(distance.loe(radiusKm));

        var query = queryFactory
                .select(program)
                .from(program)
                .where(builder)
                .orderBy(distance.asc());

        if (limit != null && limit > 0) {
            query.limit(limit);
        }

        return query.fetch();
    }
}
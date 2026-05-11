package com.moment.momentbackend.recommendation.repository;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.entity.QProgram;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
import com.moment.momentbackend.recommendation.enums.OnlinePreference;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProgramQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Program> findFilteredPrograms(RecommendationPreference preference, int childAge) {
        QProgram program = QProgram.program;
        BooleanBuilder builder = new BooleanBuilder();

        // 공개 + 모집 중 필수 필터
        builder.and(program.isPublic.isTrue());
        builder.and(program.isRecruiting.isTrue());

        // 나이 범위 필터
        builder.and(
                program.targetAgeMin.isNull().or(program.targetAgeMin.loe(childAge))
        );
        builder.and(
                program.targetAgeMax.isNull().or(program.targetAgeMax.goe(childAge))
        );

        // 지역 필터
        if (preference.getRegion() != null && !preference.getRegion().isBlank()) {
            builder.and(program.region.containsIgnoreCase(preference.getRegion()));
        }

        // 예산 필터 (enum 기준)
        if (preference.getMonthlyBudget() != null) {
            switch (preference.getMonthlyBudget()) {
                case UNDER_10 -> builder.and(program.price.loe(100000));
                case UNDER_30 -> builder.and(program.price.loe(300000));
                case UNDER_50 -> builder.and(program.price.loe(500000));
                case OVER_50 -> {} // 필터 없음
            }
        }

        // 온라인/오프라인 필터 (enum 기준)
        if (preference.getOnlinePreference() != null) {
            if (preference.getOnlinePreference() == OnlinePreference.OFFLINE_ONLY) {
                builder.and(program.classType.ne("ONLINE"));
            } else if (preference.getOnlinePreference() == OnlinePreference.ONLINE_ONLY) {
                builder.and(program.classType.eq("ONLINE"));
            }
            // BOTH면 필터 없음
        }

        // 수업 형태 필터 (enum → String 변환)
        if (preference.getClassType() != null) {
            builder.and(program.classType.eq(preference.getClassType().name()));
        }

        return queryFactory
                .selectFrom(program)
                .leftJoin(program.tags).fetchJoin()
                .where(builder)
                .distinct()
                .fetch();
    }
}
package com.moment.momentbackend.recommendation.repository;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.entity.QProgram;
import com.moment.momentbackend.recommendation.entity.RecommendationPreference;
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

        // 예산 필터
        if (preference.getMonthlyBudget() != null) {
            switch (preference.getMonthlyBudget()) {
                case "FREE" -> builder.and(program.isFree.isTrue());
                case "0-10" -> builder.and(program.price.loe(100000));
                case "10-20" -> builder.and(program.price.loe(200000));
                case "ANY" -> {} // 필터 없음
            }
        }

        // 수업 형태 필터
        if (preference.getClassType() != null && !preference.getClassType().isBlank()) {
            // ONLINE_OK면 온라인도 허용
            if ("OFFLINE_ONLY".equals(preference.getOnlinePreference())) {
                builder.and(program.classType.ne("ONLINE"));
            }
        }

        // classType 직접 필터
        if (preference.getClassType() != null && !preference.getClassType().isBlank()) {
            builder.and(program.classType.eq(preference.getClassType()));
        }

        return queryFactory
                .selectFrom(program)
                .leftJoin(program.tags).fetchJoin()
                .where(builder)
                .distinct()
                .fetch();
    }
}
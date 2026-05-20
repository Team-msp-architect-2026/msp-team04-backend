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

        builder.and(program.isPublic.isTrue());
        builder.and(program.isRecruiting.isTrue());

        builder.and(
                program.targetAgeMin.isNull().or(program.targetAgeMin.loe(childAge))
        );
        builder.and(
                program.targetAgeMax.isNull().or(program.targetAgeMax.goe(childAge))
        );

        if (preference.getRegion() != null && !preference.getRegion().isBlank()) {
            builder.and(program.region.containsIgnoreCase(preference.getRegion()));
        }

        if (preference.getMonthlyBudget() != null) {
            switch (preference.getMonthlyBudget()) {
                case FREE -> builder.and(program.isFree.isTrue().or(program.price.eq(0)));
                case ZERO_TO_TEN -> builder.and(program.price.loe(100000));
                case TEN_TO_TWENTY -> builder.and(program.price.loe(200000));
                case OVER_TWENTY, ANY -> {
                }
            }
        }

        if (preference.getOnlinePreference() != null) {
            if (preference.getOnlinePreference() == OnlinePreference.OFFLINE_ONLY) {
                builder.and(program.classType.ne("ONLINE"));
            }
        }

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

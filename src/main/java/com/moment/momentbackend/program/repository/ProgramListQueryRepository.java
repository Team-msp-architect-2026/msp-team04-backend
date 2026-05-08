package com.moment.momentbackend.program.repository;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.entity.QProgram;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProgramListQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Program> findPrograms(String status, String category,
                                      String region, Pageable pageable) {
        QProgram program = QProgram.program;
        BooleanBuilder builder = new BooleanBuilder();

        // 공개 필터 (항상)
        builder.and(program.isPublic.isTrue());

        // status 필터
        if (status != null) {
            if ("RECRUITING".equalsIgnoreCase(status)) {
                builder.and(program.isRecruiting.isTrue());
            } else if ("CLOSED".equalsIgnoreCase(status)) {
                builder.and(program.isRecruiting.isFalse());
            }
        }

        // category 필터
        if (category != null && !category.isBlank()) {
            builder.and(program.category.eq(category.toUpperCase()));
        }

        // region 필터
        if (region != null && !region.isBlank()) {
            builder.and(program.region.containsIgnoreCase(region));
        }

        // 정렬 처리
        com.querydsl.core.types.OrderSpecifier<?> order = getOrderSpecifier(program, pageable);

        List<Program> content = queryFactory
                .selectFrom(program)
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(program.count())
                .from(program)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private com.querydsl.core.types.OrderSpecifier<?> getOrderSpecifier(
            QProgram program, Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {
            if ("deadlineDate".equals(order.getProperty())) {
                return order.isAscending()
                        ? program.deadlineDate.asc().nullsLast()
                        : program.deadlineDate.desc().nullsLast();
            }
            if ("ratingAvg".equals(order.getProperty())) {
                return program.ratingAvg.desc();
            }
            if ("price".equals(order.getProperty())) {
                return order.isAscending()
                        ? program.price.asc()
                        : program.price.desc();
            }
        }
        // 기본 정렬: 마감임박순
        return program.deadlineDate.asc().nullsLast();
    }
}
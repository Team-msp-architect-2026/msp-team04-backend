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

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProgramListQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Program> findPrograms(String status, String category,
                                      String region, Pageable pageable) {
        return findPrograms(status, category, region, null, pageable);
    }

    public Page<Program> findPrograms(String status, String category,
                                      String region, String filter,
                                      Pageable pageable) {
        QProgram program = QProgram.program;
        BooleanBuilder builder = new BooleanBuilder();

        LocalDate today = LocalDate.now();

        // 공개 필터
        builder.and(program.isPublic.isTrue());

        // 마감일이 과거인 데이터는 기본 목록에서 제외
        // 단, CLOSED 조회는 과거 데이터도 볼 수 있게 둔다.
        if (status == null || !"CLOSED".equalsIgnoreCase(status)) {
            builder.and(
                    program.deadlineDate.isNull()
                            .or(program.deadlineDate.goe(today))
            );
        }

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

        // 화면 필터
        if (filter != null && !filter.isBlank() && !"ALL".equalsIgnoreCase(filter)) {
            applyScreenFilter(builder, program, filter, today);
        }

        // 정렬 처리
        com.querydsl.core.types.OrderSpecifier<?> order = getOrderSpecifier(program, pageable, filter);

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

    private void applyScreenFilter(BooleanBuilder builder,
                                   QProgram program,
                                   String filter,
                                   LocalDate today) {

        if ("URGENT".equalsIgnoreCase(filter)) {
            builder.and(program.isRecruiting.isTrue());
            builder.and(program.deadlineDate.isNotNull());
            builder.and(program.deadlineDate.goe(today));
            builder.and(program.deadlineDate.loe(today.plusDays(14)));
            return;
        }

        if ("FREE".equalsIgnoreCase(filter)) {
            builder.and(
                    program.isFree.isTrue()
                            .or(program.price.eq(0))
            );
            return;
        }

        if ("ONLINE".equalsIgnoreCase(filter)) {
            BooleanBuilder onlineBuilder = new BooleanBuilder();

            onlineBuilder.or(program.classType.containsIgnoreCase("ONLINE"));
            onlineBuilder.or(program.classType.containsIgnoreCase("온라인"));
            onlineBuilder.or(program.region.containsIgnoreCase("온라인"));
            onlineBuilder.or(program.detailAddress.containsIgnoreCase("온라인"));
            onlineBuilder.or(program.description.containsIgnoreCase("온라인"));
            onlineBuilder.or(program.description.containsIgnoreCase("비대면"));
            onlineBuilder.or(program.description.containsIgnoreCase("원격"));
            onlineBuilder.or(program.title.containsIgnoreCase("온라인"));
            onlineBuilder.or(program.title.containsIgnoreCase("비대면"));

            builder.and(onlineBuilder);
            return;
        }

        if ("PUBLIC_SUPPORT".equalsIgnoreCase(filter)) {
            builder.and(
                    program.programType.in("PUBLIC", "GOVERNMENT", "public", "government")
            );
        }
    }

    private com.querydsl.core.types.OrderSpecifier<?> getOrderSpecifier(
            QProgram program, Pageable pageable, String filter) {

        if ("URGENT".equalsIgnoreCase(filter)) {
            return program.deadlineDate.asc().nullsLast();
        }

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

        return program.deadlineDate.asc().nullsLast();
    }
}
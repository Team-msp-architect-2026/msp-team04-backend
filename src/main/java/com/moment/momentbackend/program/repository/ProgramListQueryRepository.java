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

        // MoMent MVP 모집중 탭은 공공 예약 원천 전체가 아니라
        // 3~13세 자녀 보호자에게 노출 가능한 교육/돌봄/체험성 프로그램만 대상으로 한다.
        applyMomentVisibleProgramPolicy(builder, program);

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
                .leftJoin(program.institution).fetchJoin()
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

    private void applyMomentVisibleProgramPolicy(BooleanBuilder builder, QProgram program) {
        BooleanBuilder childTargetSignal = new BooleanBuilder();

        childTargetSignal.or(program.targetAgeMin.isNotNull());
        childTargetSignal.or(program.targetAgeMax.isNotNull());

        childTargetSignal.or(program.title.containsIgnoreCase("유아"));
        childTargetSignal.or(program.title.containsIgnoreCase("어린이"));
        childTargetSignal.or(program.title.containsIgnoreCase("초등"));
        childTargetSignal.or(program.title.containsIgnoreCase("아동"));
        childTargetSignal.or(program.title.containsIgnoreCase("청소년"));
        childTargetSignal.or(program.title.containsIgnoreCase("키즈"));
        childTargetSignal.or(program.title.containsIgnoreCase("가족"));
        childTargetSignal.or(program.title.containsIgnoreCase("아이"));
        childTargetSignal.or(program.title.containsIgnoreCase("양육자"));
        childTargetSignal.or(program.title.containsIgnoreCase("부모"));
        childTargetSignal.or(program.title.containsIgnoreCase("학급"));
        childTargetSignal.or(program.title.containsIgnoreCase("돌봄"));
        childTargetSignal.or(program.title.containsIgnoreCase("놀이터"));
        childTargetSignal.or(program.title.containsIgnoreCase("키즈카페"));
        childTargetSignal.or(program.title.containsIgnoreCase("유아숲"));

        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("유아"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("어린이"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("키움센터"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("돌봄"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("키즈카페"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("유아숲"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("육아종합지원센터"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("어린이집"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("동물교실"));
        childTargetSignal.or(program.institution.institutionName.containsIgnoreCase("놀이터"));

        BooleanBuilder childFocusedActivity = new BooleanBuilder();

        childFocusedActivity.or(
                new BooleanBuilder()
                        .and(program.title.containsIgnoreCase("박물관"))
                        .and(childTargetSignal)
        );
        childFocusedActivity.or(
                new BooleanBuilder()
                        .and(program.title.containsIgnoreCase("숲체험"))
                        .and(childTargetSignal)
        );
        childFocusedActivity.or(
                new BooleanBuilder()
                        .and(program.title.containsIgnoreCase("생태"))
                        .and(childTargetSignal)
        );
        childFocusedActivity.or(
                new BooleanBuilder()
                        .and(program.title.containsIgnoreCase("목공체험"))
                        .and(childTargetSignal)
        );
        childFocusedActivity.or(
                new BooleanBuilder()
                        .and(program.title.containsIgnoreCase("메이커"))
                        .and(childTargetSignal)
        );

        BooleanBuilder allowed = new BooleanBuilder();
        allowed.or(childTargetSignal);
        allowed.or(childFocusedActivity);

        BooleanBuilder excluded = new BooleanBuilder();

        excluded.or(program.title.containsIgnoreCase("대관"));
        excluded.or(program.title.containsIgnoreCase("코트"));
        excluded.or(program.title.containsIgnoreCase("테니스장"));
        excluded.or(program.title.containsIgnoreCase("파크골프장"));
        excluded.or(program.title.containsIgnoreCase("예비군"));
        excluded.or(program.title.containsIgnoreCase("훈련장"));
        excluded.or(program.title.containsIgnoreCase("수송버스"));
        excluded.or(program.title.containsIgnoreCase("훈련복장"));
        excluded.or(program.title.containsIgnoreCase("청년센터"));
        excluded.or(program.title.containsIgnoreCase("성인대상"));
        excluded.or(program.title.containsIgnoreCase("대학생"));
        excluded.or(program.title.containsIgnoreCase("도그요가"));
        excluded.or(program.title.containsIgnoreCase("반려견"));
        excluded.or(program.title.containsIgnoreCase("웰니스"));
        excluded.or(program.title.containsIgnoreCase("자세인식"));
        excluded.or(program.title.containsIgnoreCase("요가"));
        excluded.or(program.title.containsIgnoreCase("명상"));
        excluded.or(program.title.containsIgnoreCase("정원처방"));
        excluded.or(program.title.containsIgnoreCase("조향"));
        excluded.or(program.title.containsIgnoreCase("원데이클래스"));
        excluded.or(program.title.containsIgnoreCase("성인"));
        excluded.or(program.title.containsIgnoreCase("외국인"));
        excluded.or(program.title.containsIgnoreCase("강연"));
        excluded.or(program.title.containsIgnoreCase("인사이트"));
        excluded.or(program.title.containsIgnoreCase("처방"));
        excluded.or(program.title.containsIgnoreCase("개방형교육"));
        excluded.or(program.title.containsIgnoreCase("가드너"));
        excluded.or(program.title.containsIgnoreCase("반려식물"));

        excluded.or(program.institution.institutionName.containsIgnoreCase("테니스장"));
        excluded.or(program.institution.institutionName.containsIgnoreCase("파크골프장"));
        excluded.or(program.institution.institutionName.containsIgnoreCase("예비군훈련장"));
        excluded.or(program.institution.institutionName.containsIgnoreCase("청년센터"));

        builder.and(allowed);
        builder.and(excluded.not());
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
            builder.and(program.isFree.isTrue());
            return;
        }

        if ("ONLINE".equalsIgnoreCase(filter)) {
            BooleanBuilder onlineBuilder = new BooleanBuilder();

            onlineBuilder.or(program.classType.containsIgnoreCase("ONLINE"));
            onlineBuilder.or(program.classType.containsIgnoreCase("온라인"));
            onlineBuilder.or(program.title.containsIgnoreCase("온라인"));
            onlineBuilder.or(program.title.containsIgnoreCase("비대면"));

            // description의 "온라인"은 온라인 수업이 아니라 온라인 예약/신청 안내 문구인 경우가 많아 제외한다.
            builder.and(onlineBuilder);
            return;
        }

        if ("PUBLIC_SUPPORT".equalsIgnoreCase(filter)) {
            builder.and(program.isPublic.isTrue());
            return;
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
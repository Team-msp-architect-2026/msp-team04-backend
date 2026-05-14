package com.moment.momentbackend.search.repository;

import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.entity.QInstitution;
import com.moment.momentbackend.program.entity.QProgram;
import com.moment.momentbackend.program.entity.QProgramTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchProgramQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Program> searchPrograms(String keyword, Pageable pageable) {
        QProgram program = QProgram.program;
        QInstitution institution = QInstitution.institution;
        QProgramTag programTag = QProgramTag.programTag;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(program.isPublic.isTrue());
        builder.and(keywordCondition(program, institution, programTag, keyword));

        OrderSpecifier<?> relevanceOrder = relevanceOrder(program, institution, programTag, keyword).asc();

        List<Program> content = queryFactory
                .selectFrom(program)
                .leftJoin(program.institution, institution).fetchJoin()
                .where(builder)
                .orderBy(relevanceOrder, program.deadlineDate.asc().nullsLast(), program.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(program.count())
                .from(program)
                .leftJoin(program.institution, institution)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression keywordCondition(QProgram program,
                                               QInstitution institution,
                                               QProgramTag programTag,
                                               String keyword) {
        return program.title.containsIgnoreCase(keyword)
                .or(program.description.containsIgnoreCase(keyword))
                .or(program.category.containsIgnoreCase(keyword))
                .or(program.region.containsIgnoreCase(keyword))
                .or(program.detailAddress.containsIgnoreCase(keyword))
                .or(institution.institutionName.containsIgnoreCase(keyword))
                .or(JPAExpressions
                        .selectOne()
                        .from(programTag)
                        .where(programTag.program.eq(program)
                                .and(programTag.tag.containsIgnoreCase(keyword)))
                        .exists());
    }

    private NumberExpression<Integer> relevanceOrder(QProgram program,
                                                     QInstitution institution,
                                                     QProgramTag programTag,
                                                     String keyword) {
        BooleanExpression tagMatched = JPAExpressions
                .selectOne()
                .from(programTag)
                .where(programTag.program.eq(program)
                        .and(programTag.tag.containsIgnoreCase(keyword)))
                .exists();

        return new CaseBuilder()
                .when(program.title.containsIgnoreCase(keyword)).then(0)
                .when(institution.institutionName.containsIgnoreCase(keyword)).then(1)
                .when(program.category.containsIgnoreCase(keyword)).then(2)
                .when(tagMatched).then(3)
                .otherwise(4);
    }
}

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
import java.util.Locale;

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

    public boolean existsByKeyword(String keyword) {
        QProgram program = QProgram.program;
        QInstitution institution = QInstitution.institution;
        QProgramTag programTag = QProgramTag.programTag;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(program.isPublic.isTrue());
        builder.and(keywordCondition(program, institution, programTag, keyword));

        Integer result = queryFactory
                .selectOne()
                .from(program)
                .leftJoin(program.institution, institution)
                .where(builder)
                .fetchFirst();

        return result != null;
    }

    private BooleanBuilder keywordCondition(QProgram program,
                                            QInstitution institution,
                                            QProgramTag programTag,
                                            String keyword) {
        SearchKeywordIntent intent = SearchKeywordIntent.from(keyword);
        BooleanBuilder condition = new BooleanBuilder();

        if (!intent.hasConditionKeyword()) {
            condition.and(basicKeywordCondition(program, institution, programTag, keyword));
            return condition;
        }

        if (intent.free) {
            condition.and(freeCondition(program));
        }

        if (intent.weekend) {
            condition.and(weekendCondition(program));
        }

        if (intent.care) {
            condition.and(careCondition(program));
        }

        if (intent.publicInstitution) {
            condition.and(publicInstitutionCondition(institution));
        }

        if (intent.smallClass) {
            condition.and(smallClassCondition(program));
        }

        if (!intent.remainingKeyword.isBlank()) {
            condition.and(basicKeywordCondition(program, institution, programTag, intent.remainingKeyword));
        }

        return condition;
    }

    private BooleanExpression basicKeywordCondition(QProgram program,
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

    private BooleanExpression freeCondition(QProgram program) {
        return program.isFree.isTrue()
                .or(program.price.eq(0));
    }

    private BooleanExpression weekendCondition(QProgram program) {
        return program.title.containsIgnoreCase("주말")
                .or(program.description.containsIgnoreCase("주말"))
                .or(program.classTime.containsIgnoreCase("주말"))
                .or(program.title.containsIgnoreCase("토/일"))
                .or(program.title.containsIgnoreCase("토요일"))
                .or(program.title.containsIgnoreCase("일요일"))
                .or(program.title.containsIgnoreCase("[토]"))
                .or(program.title.containsIgnoreCase("[일]"))
                .or(program.classTime.containsIgnoreCase("토"))
                .or(program.classTime.containsIgnoreCase("일"));
    }

    private BooleanExpression careCondition(QProgram program) {
        return program.category.equalsIgnoreCase("CARE")
                .or(program.title.containsIgnoreCase("돌봄"))
                .or(program.description.containsIgnoreCase("돌봄"));
    }

    private BooleanExpression publicInstitutionCondition(QInstitution institution) {
        return institution.institutionType.equalsIgnoreCase("PUBLIC")
                .or(institution.institutionName.containsIgnoreCase("공공"));
    }

    private BooleanExpression smallClassCondition(QProgram program) {
        return program.title.containsIgnoreCase("소규모")
                .or(program.description.containsIgnoreCase("소규모"));
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
    private static final class SearchKeywordIntent {

        private final boolean free;
        private final boolean weekend;
        private final boolean care;
        private final boolean publicInstitution;
        private final boolean smallClass;
        private final String remainingKeyword;

        private SearchKeywordIntent(
                boolean free,
                boolean weekend,
                boolean care,
                boolean publicInstitution,
                boolean smallClass,
                String remainingKeyword
        ) {
            this.free = free;
            this.weekend = weekend;
            this.care = care;
            this.publicInstitution = publicInstitution;
            this.smallClass = smallClass;
            this.remainingKeyword = remainingKeyword;
        }

        private static SearchKeywordIntent from(String keyword) {
            String normalized = keyword.trim().replaceAll("\\s+", " ");
            String compact = normalized.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");

            boolean free = compact.contains("무료") || compact.contains("무상");
            boolean weekend = compact.contains("주말")
                    || compact.contains("토요일")
                    || compact.contains("일요일")
                    || compact.contains("토/일")
                    || compact.contains("토일");
            boolean care = compact.contains("돌봄") || compact.contains("케어");
            boolean publicInstitution = compact.contains("공공기관")
                    || compact.contains("공공시설")
                    || compact.contains("공공");
            boolean smallClass = compact.contains("소규모");

            String remainingKeyword = normalized;
            remainingKeyword = removeKeyword(remainingKeyword, "무료");
            remainingKeyword = removeKeyword(remainingKeyword, "무상");
            remainingKeyword = removeKeyword(remainingKeyword, "주말");
            remainingKeyword = removeKeyword(remainingKeyword, "토요일");
            remainingKeyword = removeKeyword(remainingKeyword, "일요일");
            remainingKeyword = removeKeyword(remainingKeyword, "토/일");
            remainingKeyword = removeKeyword(remainingKeyword, "토일");
            remainingKeyword = removeKeyword(remainingKeyword, "돌봄");
            remainingKeyword = removeKeyword(remainingKeyword, "케어");
            remainingKeyword = removeKeyword(remainingKeyword, "공공기관");
            remainingKeyword = removeKeyword(remainingKeyword, "공공시설");
            remainingKeyword = removeKeyword(remainingKeyword, "공공");
            remainingKeyword = removeKeyword(remainingKeyword, "기관");
            remainingKeyword = removeKeyword(remainingKeyword, "소규모");
            remainingKeyword = removeKeyword(remainingKeyword, "프로그램");
            remainingKeyword = removeKeyword(remainingKeyword, "수업");
            remainingKeyword = removeKeyword(remainingKeyword, "강의");
            remainingKeyword = removeKeyword(remainingKeyword, "클래스");
            remainingKeyword = remainingKeyword.trim().replaceAll("\\s+", " ");

            return new SearchKeywordIntent(
                    free,
                    weekend,
                    care,
                    publicInstitution,
                    smallClass,
                    remainingKeyword
            );
        }

        private boolean hasConditionKeyword() {
            return free || weekend || care || publicInstitution || smallClass;
        }

        private static String removeKeyword(String value, String keyword) {
            return value.replace(keyword, " ");
        }
    }


}

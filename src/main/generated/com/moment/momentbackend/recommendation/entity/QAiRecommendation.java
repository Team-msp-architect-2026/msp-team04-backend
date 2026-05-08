package com.moment.momentbackend.recommendation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAiRecommendation is a Querydsl query type for AiRecommendation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAiRecommendation extends EntityPathBase<AiRecommendation> {

    private static final long serialVersionUID = -419011470L;

    public static final QAiRecommendation aiRecommendation = new QAiRecommendation("aiRecommendation");

    public final NumberPath<Long> childId = createNumber("childId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isTop3 = createBoolean("isTop3");

    public final NumberPath<Long> preferenceId = createNumber("preferenceId", Long.class);

    public final NumberPath<Long> programId = createNumber("programId", Long.class);

    public final NumberPath<Integer> rankNo = createNumber("rankNo", Integer.class);

    public final StringPath recommendReason = createString("recommendReason");

    public final NumberPath<java.math.BigDecimal> scoreAge = createNumber("scoreAge", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> scoreBudget = createNumber("scoreBudget", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> scoreClassType = createNumber("scoreClassType", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> scoreDistance = createNumber("scoreDistance", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> scoreKeyword = createNumber("scoreKeyword", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> scoreRecruiting = createNumber("scoreRecruiting", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> scoreReview = createNumber("scoreReview", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalScore = createNumber("totalScore", java.math.BigDecimal.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QAiRecommendation(String variable) {
        super(AiRecommendation.class, forVariable(variable));
    }

    public QAiRecommendation(Path<? extends AiRecommendation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAiRecommendation(PathMetadata metadata) {
        super(AiRecommendation.class, metadata);
    }

}


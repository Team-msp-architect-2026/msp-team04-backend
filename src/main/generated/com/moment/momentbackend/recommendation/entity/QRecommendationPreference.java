package com.moment.momentbackend.recommendation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRecommendationPreference is a Querydsl query type for RecommendationPreference
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecommendationPreference extends EntityPathBase<RecommendationPreference> {

    private static final long serialVersionUID = 1089468133L;

    public static final QRecommendationPreference recommendationPreference = new QRecommendationPreference("recommendationPreference");

    public final NumberPath<Long> childId = createNumber("childId", Long.class);

    public final StringPath classType = createString("classType");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath monthlyBudget = createString("monthlyBudget");

    public final StringPath moveTime = createString("moveTime");

    public final StringPath onlinePreference = createString("onlinePreference");

    public final StringPath region = createString("region");

    public final StringPath transportType = createString("transportType");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRecommendationPreference(String variable) {
        super(RecommendationPreference.class, forVariable(variable));
    }

    public QRecommendationPreference(Path<? extends RecommendationPreference> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRecommendationPreference(PathMetadata metadata) {
        super(RecommendationPreference.class, metadata);
    }

}


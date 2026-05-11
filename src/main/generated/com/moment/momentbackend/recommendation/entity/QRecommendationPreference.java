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

    public final EnumPath<com.moment.momentbackend.recommendation.enums.ClassType> classType = createEnum("classType", com.moment.momentbackend.recommendation.enums.ClassType.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.moment.momentbackend.recommendation.enums.MonthlyBudget> monthlyBudget = createEnum("monthlyBudget", com.moment.momentbackend.recommendation.enums.MonthlyBudget.class);

    public final EnumPath<com.moment.momentbackend.recommendation.enums.MoveTime> moveTime = createEnum("moveTime", com.moment.momentbackend.recommendation.enums.MoveTime.class);

    public final EnumPath<com.moment.momentbackend.recommendation.enums.OnlinePreference> onlinePreference = createEnum("onlinePreference", com.moment.momentbackend.recommendation.enums.OnlinePreference.class);

    public final StringPath region = createString("region");

    public final EnumPath<com.moment.momentbackend.recommendation.enums.TransportType> transportType = createEnum("transportType", com.moment.momentbackend.recommendation.enums.TransportType.class);

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


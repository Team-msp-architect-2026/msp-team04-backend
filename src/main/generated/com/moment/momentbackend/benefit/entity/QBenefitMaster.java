package com.moment.momentbackend.benefit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBenefitMaster is a Querydsl query type for BenefitMaster
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBenefitMaster extends EntityPathBase<BenefitMaster> {

    private static final long serialVersionUID = 432128128L;

    public static final QBenefitMaster benefitMaster = new QBenefitMaster("benefitMaster");

    public final StringPath applyLink = createString("applyLink");

    public final StringPath benefitName = createString("benefitName");

    public final StringPath benefitType = createString("benefitType");

    public final StringPath conditionDescription = createString("conditionDescription");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath externalId = createString("externalId");

    public final StringPath externalSource = createString("externalSource");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final DateTimePath<java.time.LocalDateTime> lastSyncedAt = createDateTime("lastSyncedAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> maxAge = createNumber("maxAge", Integer.class);

    public final NumberPath<Integer> minAge = createNumber("minAge", Integer.class);

    public final StringPath region = createString("region");

    public final NumberPath<Integer> supportAmount = createNumber("supportAmount", Integer.class);

    public final StringPath supportDescription = createString("supportDescription");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBenefitMaster(String variable) {
        super(BenefitMaster.class, forVariable(variable));
    }

    public QBenefitMaster(Path<? extends BenefitMaster> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBenefitMaster(PathMetadata metadata) {
        super(BenefitMaster.class, metadata);
    }

}


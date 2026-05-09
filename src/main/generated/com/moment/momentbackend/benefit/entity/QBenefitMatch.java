package com.moment.momentbackend.benefit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBenefitMatch is a Querydsl query type for BenefitMatch
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBenefitMatch extends EntityPathBase<BenefitMatch> {

    private static final long serialVersionUID = 706676711L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBenefitMatch benefitMatch = new QBenefitMatch("benefitMatch");

    public final QBenefitMaster benefit;

    public final NumberPath<Long> childId = createNumber("childId", Long.class);

    public final NumberPath<Integer> expectedMonthlySaving = createNumber("expectedMonthlySaving", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> matchedAt = createDateTime("matchedAt", java.time.LocalDateTime.class);

    public final StringPath matchStatus = createString("matchStatus");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QBenefitMatch(String variable) {
        this(BenefitMatch.class, forVariable(variable), INITS);
    }

    public QBenefitMatch(Path<? extends BenefitMatch> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBenefitMatch(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBenefitMatch(PathMetadata metadata, PathInits inits) {
        this(BenefitMatch.class, metadata, inits);
    }

    public QBenefitMatch(Class<? extends BenefitMatch> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.benefit = inits.isInitialized("benefit") ? new QBenefitMaster(forProperty("benefit")) : null;
    }

}


package com.moment.momentbackend.report.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAiReport is a Querydsl query type for AiReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAiReport extends EntityPathBase<AiReport> {

    private static final long serialVersionUID = -916451992L;

    public static final QAiReport aiReport = new QAiReport("aiReport");

    public final NumberPath<java.math.BigDecimal> aiMatchScore = createNumber("aiMatchScore", java.math.BigDecimal.class);

    public final NumberPath<Long> childId = createNumber("childId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath summaryMessage = createString("summaryMessage");

    public final NumberPath<Integer> totalFreeProgramCount = createNumber("totalFreeProgramCount", Integer.class);

    public final NumberPath<Integer> totalMonthlySaving = createNumber("totalMonthlySaving", Integer.class);

    public final NumberPath<Integer> totalRecommendCount = createNumber("totalRecommendCount", Integer.class);

    public final NumberPath<Integer> totalSupportCount = createNumber("totalSupportCount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QAiReport(String variable) {
        super(AiReport.class, forVariable(variable));
    }

    public QAiReport(Path<? extends AiReport> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAiReport(PathMetadata metadata) {
        super(AiReport.class, metadata);
    }

}


package com.moment.momentbackend.program.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProgram is a Querydsl query type for Program
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProgram extends EntityPathBase<Program> {

    private static final long serialVersionUID = -1412839202L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProgram program = new QProgram("program");

    public final StringPath category = createString("category");

    public final StringPath classTime = createString("classTime");

    public final StringPath classType = createString("classType");

    public final StringPath contactPhone = createString("contactPhone");

    public final StringPath contactUrl = createString("contactUrl");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath curriculum = createString("curriculum");

    public final DatePath<java.time.LocalDate> deadlineDate = createDate("deadlineDate", java.time.LocalDate.class);

    public final StringPath description = createString("description");

    public final StringPath detailAddress = createString("detailAddress");

    public final StringPath externalId = createString("externalId");

    public final StringPath externalSource = createString("externalSource");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final QInstitution institution;

    public final BooleanPath isFree = createBoolean("isFree");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final BooleanPath isRecruiting = createBoolean("isRecruiting");

    public final DateTimePath<java.time.LocalDateTime> lastSyncedAt = createDateTime("lastSyncedAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    public final NumberPath<Integer> maxCapacity = createNumber("maxCapacity", Integer.class);

    public final DatePath<java.time.LocalDate> operationEnd = createDate("operationEnd", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> operationStart = createDate("operationStart", java.time.LocalDate.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath programType = createString("programType");

    public final NumberPath<java.math.BigDecimal> ratingAvg = createNumber("ratingAvg", java.math.BigDecimal.class);

    public final StringPath region = createString("region");

    public final NumberPath<Integer> remainCapacity = createNumber("remainCapacity", Integer.class);

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public final ListPath<ProgramTag, QProgramTag> tags = this.<ProgramTag, QProgramTag>createList("tags", ProgramTag.class, QProgramTag.class, PathInits.DIRECT2);

    public final NumberPath<Integer> targetAgeMax = createNumber("targetAgeMax", Integer.class);

    public final NumberPath<Integer> targetAgeMin = createNumber("targetAgeMin", Integer.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QProgram(String variable) {
        this(Program.class, forVariable(variable), INITS);
    }

    public QProgram(Path<? extends Program> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProgram(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProgram(PathMetadata metadata, PathInits inits) {
        this(Program.class, metadata, inits);
    }

    public QProgram(Class<? extends Program> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.institution = inits.isInitialized("institution") ? new QInstitution(forProperty("institution")) : null;
    }

}


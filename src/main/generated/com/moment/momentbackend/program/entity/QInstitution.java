package com.moment.momentbackend.program.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInstitution is a Querydsl query type for Institution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstitution extends EntityPathBase<Institution> {

    private static final long serialVersionUID = -1493986542L;

    public static final QInstitution institution = new QInstitution("institution");

    public final StringPath address = createString("address");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath externalId = createString("externalId");

    public final StringPath externalSource = createString("externalSource");

    public final StringPath homepageUrl = createString("homepageUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath institutionName = createString("institutionName");

    public final StringPath institutionType = createString("institutionType");

    public final DateTimePath<java.time.LocalDateTime> lastSyncedAt = createDateTime("lastSyncedAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    public final StringPath phone = createString("phone");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QInstitution(String variable) {
        super(Institution.class, forVariable(variable));
    }

    public QInstitution(Path<? extends Institution> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInstitution(PathMetadata metadata) {
        super(Institution.class, metadata);
    }

}


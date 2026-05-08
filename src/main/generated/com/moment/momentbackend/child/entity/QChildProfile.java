package com.moment.momentbackend.child.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChildProfile is a Querydsl query type for ChildProfile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChildProfile extends EntityPathBase<ChildProfile> {

    private static final long serialVersionUID = -455865685L;

    public static final QChildProfile childProfile = new QChildProfile("childProfile");

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final StringPath childName = createString("childName");

    public final ListPath<ChildConcern, QChildConcern> concerns = this.<ChildConcern, QChildConcern>createList("concerns", ChildConcern.class, QChildConcern.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QChildProfile(String variable) {
        super(ChildProfile.class, forVariable(variable));
    }

    public QChildProfile(Path<? extends ChildProfile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChildProfile(PathMetadata metadata) {
        super(ChildProfile.class, metadata);
    }

}


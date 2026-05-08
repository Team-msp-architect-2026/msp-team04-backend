package com.moment.momentbackend.child.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChildConcern is a Querydsl query type for ChildConcern
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChildConcern extends EntityPathBase<ChildConcern> {

    private static final long serialVersionUID = 804584354L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChildConcern childConcern = new QChildConcern("childConcern");

    public final QChildProfile childProfile;

    public final StringPath concern = createString("concern");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QChildConcern(String variable) {
        this(ChildConcern.class, forVariable(variable), INITS);
    }

    public QChildConcern(Path<? extends ChildConcern> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChildConcern(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChildConcern(PathMetadata metadata, PathInits inits) {
        this(ChildConcern.class, metadata, inits);
    }

    public QChildConcern(Class<? extends ChildConcern> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.childProfile = inits.isInitialized("childProfile") ? new QChildProfile(forProperty("childProfile")) : null;
    }

}


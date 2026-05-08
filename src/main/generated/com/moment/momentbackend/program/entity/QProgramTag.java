package com.moment.momentbackend.program.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProgramTag is a Querydsl query type for ProgramTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProgramTag extends EntityPathBase<ProgramTag> {

    private static final long serialVersionUID = 786917852L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProgramTag programTag = new QProgramTag("programTag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QProgram program;

    public final StringPath tag = createString("tag");

    public QProgramTag(String variable) {
        this(ProgramTag.class, forVariable(variable), INITS);
    }

    public QProgramTag(Path<? extends ProgramTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProgramTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProgramTag(PathMetadata metadata, PathInits inits) {
        this(ProgramTag.class, metadata, inits);
    }

    public QProgramTag(Class<? extends ProgramTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.program = inits.isInitialized("program") ? new QProgram(forProperty("program"), inits.get("program")) : null;
    }

}


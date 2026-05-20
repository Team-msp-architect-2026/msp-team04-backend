package com.moment.momentbackend.search.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAiSearchSuggestion is a Querydsl query type for AiSearchSuggestion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAiSearchSuggestion extends EntityPathBase<AiSearchSuggestion> {

    private static final long serialVersionUID = -1702705036L;

    public static final QAiSearchSuggestion aiSearchSuggestion = new QAiSearchSuggestion("aiSearchSuggestion");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isGlobal = createBoolean("isGlobal");

    public final StringPath suggestionText = createString("suggestionText");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QAiSearchSuggestion(String variable) {
        super(AiSearchSuggestion.class, forVariable(variable));
    }

    public QAiSearchSuggestion(Path<? extends AiSearchSuggestion> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAiSearchSuggestion(PathMetadata metadata) {
        super(AiSearchSuggestion.class, metadata);
    }

}


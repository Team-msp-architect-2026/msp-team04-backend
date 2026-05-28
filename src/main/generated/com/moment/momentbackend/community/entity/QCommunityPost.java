package com.moment.momentbackend.community.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommunityPost is a Querydsl query type for CommunityPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunityPost extends EntityPathBase<CommunityPost> {

    private static final long serialVersionUID = 1217827838L;

    public static final QCommunityPost communityPost = new QCommunityPost("communityPost");

    public final EnumPath<com.moment.momentbackend.community.type.PostCategory> category = createEnum("category", com.moment.momentbackend.community.type.PostCategory.class);

    public final StringPath childAge = createString("childAge");

    public final NumberPath<Integer> commentCount = createNumber("commentCount", Integer.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QCommunityPost(String variable) {
        super(CommunityPost.class, forVariable(variable));
    }

    public QCommunityPost(Path<? extends CommunityPost> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommunityPost(PathMetadata metadata) {
        super(CommunityPost.class, metadata);
    }

}


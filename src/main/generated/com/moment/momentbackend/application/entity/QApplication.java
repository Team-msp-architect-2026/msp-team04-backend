package com.moment.momentbackend.application.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QApplication is a Querydsl query type for Application
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApplication extends EntityPathBase<Application> {

    private static final long serialVersionUID = -346096930L;

    public static final QApplication application = new QApplication("application");

    public final BooleanPath agreePrivacy = createBoolean("agreePrivacy");

    public final BooleanPath agreeTerms = createBoolean("agreeTerms");

    public final StringPath aiStartMessage = createString("aiStartMessage");

    public final StringPath applicantName = createString("applicantName");

    public final EnumPath<com.moment.momentbackend.application.type.ApplicationStatus> applicationStatus = createEnum("applicationStatus", com.moment.momentbackend.application.type.ApplicationStatus.class);

    public final DateTimePath<java.time.LocalDateTime> appliedAt = createDateTime("appliedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> childId = createNumber("childId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath parentName = createString("parentName");

    public final StringPath phone = createString("phone");

    public final NumberPath<Long> programId = createNumber("programId", Long.class);

    public final StringPath requestNote = createString("requestNote");

    public final NumberPath<Integer> reserveNo = createNumber("reserveNo", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> seatLockedUntil = createDateTime("seatLockedUntil", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QApplication(String variable) {
        super(Application.class, forVariable(variable));
    }

    public QApplication(Path<? extends Application> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApplication(PathMetadata metadata) {
        super(Application.class, metadata);
    }

}


package com.moment.momentbackend.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = -541846370L;

    public static final QPayment payment = new QPayment("payment");

    public final NumberPath<Long> applicationId = createNumber("applicationId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> approvedAt = createDateTime("approvedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> cancelledAt = createDateTime("cancelledAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath failureCode = createString("failureCode");

    public final StringPath failureMessage = createString("failureMessage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath orderId = createString("orderId");

    public final NumberPath<Integer> paymentAmount = createNumber("paymentAmount", Integer.class);

    public final StringPath paymentKey = createString("paymentKey");

    public final EnumPath<com.moment.momentbackend.payment.type.PaymentMethod> paymentMethod = createEnum("paymentMethod", com.moment.momentbackend.payment.type.PaymentMethod.class);

    public final EnumPath<com.moment.momentbackend.payment.type.PaymentStatus> paymentStatus = createEnum("paymentStatus", com.moment.momentbackend.payment.type.PaymentStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QPayment(String variable) {
        super(Payment.class, forVariable(variable));
    }

    public QPayment(Path<? extends Payment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayment(PathMetadata metadata) {
        super(Payment.class, metadata);
    }

}


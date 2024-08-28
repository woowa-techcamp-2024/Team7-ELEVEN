package com.wootecam.luckyvickyauction.infra.entity.receipt;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReceiptEntity is a Querydsl query type for ReceiptEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReceiptEntity extends EntityPathBase<ReceiptEntity> {

    private static final long serialVersionUID = -789824883L;

    public static final QReceiptEntity receiptEntity = new QReceiptEntity("receiptEntity");

    public final NumberPath<Long> auctionId = createNumber("auctionId", Long.class);

    public final NumberPath<Long> buyerId = createNumber("buyerId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt",
            java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public final EnumPath<com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus> receiptStatus = createEnum(
            "receiptStatus", com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus.class);

    public final NumberPath<Long> sellerId = createNumber("sellerId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt",
            java.time.LocalDateTime.class);

    public QReceiptEntity(String variable) {
        super(ReceiptEntity.class, forVariable(variable));
    }

    public QReceiptEntity(Path<? extends ReceiptEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReceiptEntity(PathMetadata metadata) {
        super(ReceiptEntity.class, metadata);
    }

}


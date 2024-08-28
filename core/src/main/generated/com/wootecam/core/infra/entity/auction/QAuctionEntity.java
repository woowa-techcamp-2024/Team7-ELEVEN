package com.wootecam.luckyvickyauction.infra.entity.auction;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuctionEntity is a Querydsl query type for AuctionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionEntity extends EntityPathBase<AuctionEntity> {

    private static final long serialVersionUID = 1277505315L;

    public static final QAuctionEntity auctionEntity = new QAuctionEntity("auctionEntity");

    public final NumberPath<Long> currentPrice = createNumber("currentPrice", Long.class);

    public final NumberPath<Long> currentStock = createNumber("currentStock", Long.class);

    public final DateTimePath<java.time.LocalDateTime> finishedAt = createDateTime("finishedAt",
            java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isShowStock = createBoolean("isShowStock");

    public final NumberPath<Long> maximumPurchaseLimitCount = createNumber("maximumPurchaseLimitCount", Long.class);

    public final NumberPath<Long> originPrice = createNumber("originPrice", Long.class);

    public final NumberPath<Long> originStock = createNumber("originStock", Long.class);

    public final SimplePath<com.wootecam.luckyvickyauction.domain.entity.type.PricePolicy> pricePolicy = createSimple(
            "pricePolicy", com.wootecam.luckyvickyauction.domain.entity.type.PricePolicy.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Long> sellerId = createNumber("sellerId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startedAt = createDateTime("startedAt",
            java.time.LocalDateTime.class);

    public final ComparablePath<java.time.Duration> variationDuration = createComparable("variationDuration",
            java.time.Duration.class);

    public QAuctionEntity(String variable) {
        super(AuctionEntity.class, forVariable(variable));
    }

    public QAuctionEntity(Path<? extends AuctionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuctionEntity(PathMetadata metadata) {
        super(AuctionEntity.class, metadata);
    }

}


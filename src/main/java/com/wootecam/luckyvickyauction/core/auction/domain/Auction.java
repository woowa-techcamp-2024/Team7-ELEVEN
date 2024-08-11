package com.wootecam.luckyvickyauction.core.auction.domain;

import java.time.Duration;
import java.time.ZonedDateTime;
import lombok.Builder;

public class Auction {
    private final Long sellerId;
    private final String productName;
    private long originPrice;
    private long currentPrice;
    private int stock;
    private int maximumPurchaseLimitCount;
    private PricePolicy pricePolicy;
    private Duration variationDuration;
    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;
    private boolean isShowStock;

    @Builder
    private Auction(final ZonedDateTime startedAt, final Long sellerId, final String productName,
                    final long originPrice,
                    final int stock,
                    final int maximumPurchaseLimitCount, final PricePolicy pricePolicy,
                    final Duration variationDuration,
                    final ZonedDateTime finishedAt,
                    final boolean isShowStock
    ) {
        this.startedAt = startedAt;
        this.sellerId = sellerId;
        this.productName = productName;
        this.currentPrice = originPrice;
        this.originPrice = originPrice;
        this.stock = stock;
        this.maximumPurchaseLimitCount = maximumPurchaseLimitCount;
        this.pricePolicy = pricePolicy;
        this.variationDuration = variationDuration;
        this.finishedAt = finishedAt;
        this.isShowStock = isShowStock;

        pricePolicy.validate(originPrice);
    }
}

package com.wootecam.luckyvickyauction.core.auction.domain;

import java.time.Duration;
import java.time.ZonedDateTime;
import lombok.Builder;

public class Auction {
    private final Long sellerId;
    private final String productName;
    private int originPrice;
    private int currentPrice;
    private int stock;
    private int maximumPurchaseLimitCount;
    private AuctionType auctionType;
    private int variationWidth;
    private Duration variationDuration;
    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;

    @Builder
    private Auction(final ZonedDateTime startedAt, final Long sellerId, final String productName, final int originPrice,
                    final int stock,
                    final int maximumPurchaseLimitCount, final AuctionType auctionType, final int variationWidth,
                    final Duration variationDuration,
                    final ZonedDateTime finishedAt) {
        this.startedAt = startedAt;
        this.sellerId = sellerId;
        this.productName = productName;
        this.currentPrice = originPrice;
        this.originPrice = originPrice;
        this.stock = stock;
        this.maximumPurchaseLimitCount = maximumPurchaseLimitCount;
        this.auctionType = auctionType;
        this.variationWidth = variationWidth;
        this.variationDuration = variationDuration;
        this.finishedAt = finishedAt;
    }
}

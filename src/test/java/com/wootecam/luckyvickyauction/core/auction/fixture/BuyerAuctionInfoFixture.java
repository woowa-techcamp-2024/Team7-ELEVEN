package com.wootecam.luckyvickyauction.core.auction.fixture;

import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BuyerAuctionInfoFixture {
    private Long auctionId = 1L;
    private Long sellerId = 2L;
    private String productName = "테스트 상품";
    private long originPrice = 10000;
    private long currentPrice = 5000;
    private Long stock = 100L;
    private long maximumPurchaseLimitCount = 10;
    private PricePolicy pricePolicy = PricePolicy.createPercentagePricePolicy(10);
    private Duration variationDuration = Duration.ofMinutes(1L);
    private ZonedDateTime startedAt = ZonedDateTime.of(2024, 8, 15, 14, 18, 0, 0, ZoneId.of("Asia/Seoul"));
    private ZonedDateTime finishedAt = ZonedDateTime.of(2024, 8, 15, 15, 18, 0, 0, ZoneId.of("Asia/Seoul"));

    public static BuyerAuctionInfoFixture create() {
        return new BuyerAuctionInfoFixture();
    }

    public BuyerAuctionInfoFixture auctionId(Long auctionId) {
        this.auctionId = auctionId;
        return this;
    }

    public BuyerAuctionInfoFixture sellerId(Long sellerId) {
        this.sellerId = sellerId;
        return this;
    }

    public BuyerAuctionInfoFixture productName(String productName) {
        this.productName = productName;
        return this;
    }

    public BuyerAuctionInfoFixture originPrice(long originPrice) {
        this.originPrice = originPrice;
        return this;
    }

    public BuyerAuctionInfoFixture currentPrice(long currentPrice) {
        this.currentPrice = currentPrice;
        return this;
    }

    public BuyerAuctionInfoFixture stock(Long stock) {
        this.stock = stock;
        return this;
    }

    public BuyerAuctionInfoFixture maximumPurchaseLimitCount(long maximumPurchaseLimitCount) {
        this.maximumPurchaseLimitCount = maximumPurchaseLimitCount;
        return this;
    }

    public BuyerAuctionInfoFixture pricePolicy(PricePolicy pricePolicy) {
        this.pricePolicy = pricePolicy;
        return this;
    }

    public BuyerAuctionInfoFixture variationDuration(Duration variationDuration) {
        this.variationDuration = variationDuration;
        return this;
    }

    public BuyerAuctionInfoFixture startedAt(ZonedDateTime startedAt) {
        this.startedAt = startedAt;
        return this;
    }

    public BuyerAuctionInfoFixture finishedAt(ZonedDateTime finishedAt) {
        this.finishedAt = finishedAt;
        return this;
    }

    public BuyerAuctionInfo build() {
        return new BuyerAuctionInfo(
                auctionId,
                sellerId,
                productName,
                originPrice,
                currentPrice,
                stock,
                maximumPurchaseLimitCount,
                pricePolicy,
                variationDuration,
                startedAt,
                finishedAt
        );
    }
}

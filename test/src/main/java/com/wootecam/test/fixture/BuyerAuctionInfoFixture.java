package com.wootecam.test.fixture;

import com.wootecam.core.domain.entity.type.PricePolicy;
import com.wootecam.core.dto.auction.info.BuyerAuctionInfo;
import java.time.Duration;
import java.time.LocalDateTime;

public class BuyerAuctionInfoFixture {
    private Long auctionId = 1L;
    private Long sellerId = 2L;
    private String productName = "테스트 상품";
    private long originPrice = 10000;
    private long currentPrice = 5000;
    private long originStock = 100L;
    private Long currentStock = 100L;
    private long maximumPurchaseLimitCount = 10;
    private PricePolicy pricePolicy = PricePolicy.createPercentagePricePolicy(10);
    private Duration variationDuration = Duration.ofMinutes(1L);
    private LocalDateTime startedAt = LocalDateTime.of(2024, 8, 15, 14, 18, 0, 0);
    private LocalDateTime finishedAt = LocalDateTime.of(2024, 8, 15, 15, 18, 0, 0);

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

    public BuyerAuctionInfoFixture originStock(Long originStock) {
        this.originStock = originStock;
        return this;
    }

    public BuyerAuctionInfoFixture currentStock(Long currentStock) {
        this.currentStock = currentStock;
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

    public BuyerAuctionInfoFixture startedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
        return this;
    }

    public BuyerAuctionInfoFixture finishedAt(LocalDateTime finishedAt) {
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
                originStock,
                currentStock,
                maximumPurchaseLimitCount,
                pricePolicy,
                variationDuration,
                startedAt,
                finishedAt
        );
    }
}

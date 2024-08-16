package com.wootecam.luckyvickyauction.core.auction.fixture;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import java.time.Duration;
import java.time.ZonedDateTime;

public class AuctionFixture {
    private AuctionFixture() {
    }

    public static Auction createRunningAuction() {
        ZonedDateTime now = ZonedDateTime.now();

        return Auction.builder()
                .sellerId(1L)
                .productName("productName")
                .originPrice(10000L)
                .currentPrice(10000L)
                .originStock(100L)
                .currentStock(100L)
                .maximumPurchaseLimitCount(10L)
                .pricePolicy(new ConstantPricePolicy(1000L))
                .variationDuration(Duration.ofMinutes(10L))
                .startedAt(now.minusMinutes(30))
                .finishedAt(now.plusMinutes(30))
                .isShowStock(true)
                .build();
    }

    public static Auction createWaitingAuction() {
        ZonedDateTime now = ZonedDateTime.now();

        return Auction.builder()
                .sellerId(1L)
                .productName("productName")
                .originPrice(10000L)
                .currentPrice(10000L)
                .originStock(100L)
                .currentStock(100L)
                .maximumPurchaseLimitCount(10L)
                .pricePolicy(new ConstantPricePolicy(1000L))
                .variationDuration(Duration.ofMinutes(10L))
                .startedAt(now.plusHours(1))
                .finishedAt(now.plusHours(2))
                .isShowStock(true)
                .build();
    }

    public static Auction createFinishedAuction() {
        ZonedDateTime now = ZonedDateTime.now();

        return Auction.builder()
                .sellerId(1L)
                .productName("productName")
                .originPrice(10000L)
                .currentPrice(10000L)
                .originStock(100L)
                .currentStock(100L)
                .maximumPurchaseLimitCount(10L)
                .pricePolicy(new ConstantPricePolicy(1000L))
                .variationDuration(Duration.ofMinutes(10L))
                .startedAt(now.minusHours(2))
                .finishedAt(now.minusHours(1))
                .isShowStock(true)
                .build();
    }

    public static Auction createSoldOutAuction() {
        ZonedDateTime now = ZonedDateTime.now();

        return Auction.builder()
                .sellerId(1L)
                .productName("productName")
                .originPrice(10000L)
                .currentPrice(10000L)
                .originStock(100L)
                .currentStock(0L)
                .maximumPurchaseLimitCount(10L)
                .pricePolicy(new ConstantPricePolicy(1000L))
                .variationDuration(Duration.ofMinutes(10L))
                .startedAt(now.minusMinutes(30))
                .finishedAt(now.plusMinutes(30))
                .isShowStock(true)
                .build();
    }
}

package com.wootecam.luckyvickyauction.core.auction.fixture;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import java.time.Duration;
import java.time.ZonedDateTime;

public class AuctionFixture {
    private AuctionFixture() {
    }

    public static Auction createRunningAuction() {
        return Auction.builder()
                .sellerId(1L)
                .productName("productName")
                .originPrice(10000L)
                .currentPrice(10000L)
                .originStock(100L)
                .currentStock(100L)
                .maximumPurchaseLimitCount(10L)
                .pricePolicy(new ConstantPricePolicy(1000L))
                .variationDuration(Duration.ofMinutes(1L))
                .startedAt(ZonedDateTime.now().minusHours(1))
                .finishedAt(ZonedDateTime.now().plusHours(1))
                .isShowStock(true)
                .build();
    }

    public static Auction createWaitingAuction() {
        return Auction.builder()
                .sellerId(1L)
                .productName("productName")
                .originPrice(10000L)
                .currentPrice(10000L)
                .originStock(100L)
                .currentStock(100L)
                .maximumPurchaseLimitCount(10L)
                .pricePolicy(new ConstantPricePolicy(1000L))
                .variationDuration(Duration.ofMinutes(1L))
                .startedAt(ZonedDateTime.now().plusHours(1))
                .finishedAt(ZonedDateTime.now().plusHours(2))
                .isShowStock(true)
                .build();
    }

    public static Auction createFinishedAuction() {
        return Auction.builder()
                .sellerId(1L)
                .productName("productName")
                .originPrice(10000L)
                .currentPrice(10000L)
                .originStock(100L)
                .currentStock(100L)
                .maximumPurchaseLimitCount(10L)
                .pricePolicy(new ConstantPricePolicy(1000L))
                .variationDuration(Duration.ofMinutes(1L))
                .startedAt(ZonedDateTime.now().minusHours(2))
                .finishedAt(ZonedDateTime.now().minusHours(1))
                .isShowStock(true)
                .build();
    }
}

package com.wootecam.luckyvickyauction.core.auction.controller.dto;

import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import java.time.Duration;
import java.time.ZonedDateTime;

public record CreateAuctionRequest(
        String productName,
        long originPrice,
        long stock,
        long maximumPurchaseLimitCount,
        PricePolicy pricePolicy,
        Duration variationDuration,
        ZonedDateTime startedAt,
        ZonedDateTime finishedAt,
        boolean isShowStock
) {
}

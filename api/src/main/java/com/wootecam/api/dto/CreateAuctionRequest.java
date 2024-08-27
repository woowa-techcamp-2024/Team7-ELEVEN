package com.wootecam.api.dto;

import com.wootecam.core.domain.entity.type.PricePolicy;
import java.time.Duration;
import java.time.LocalDateTime;

public record CreateAuctionRequest(
        String productName,
        long originPrice,
        long stock,
        long maximumPurchaseLimitCount,
        PricePolicy pricePolicy,
        Duration variationDuration,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        boolean isShowStock
) {
}

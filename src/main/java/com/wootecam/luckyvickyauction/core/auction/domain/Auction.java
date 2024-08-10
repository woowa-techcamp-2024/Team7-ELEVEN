package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
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
    private PricePolicy pricePolicy;
    private int variationWidth;
    private Duration variationDuration;
    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;
    private boolean isShowStock;

    @Builder
    private Auction(final ZonedDateTime startedAt, final Long sellerId, final String productName, final int originPrice,
                    final int stock,
                    final int maximumPurchaseLimitCount, final PricePolicy pricePolicy, final int variationWidth,
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
        this.variationWidth = variationWidth;
        this.variationDuration = variationDuration;
        this.finishedAt = finishedAt;
        this.isShowStock = isShowStock;

        validatePriceShouldBeBiggerThanVariationWidth(originPrice, variationWidth);
    }

    private void validatePriceShouldBeBiggerThanVariationWidth(int originPrice, int variationWidth) {
        if (originPrice <= variationWidth) {
            throw new BadRequestException(
                    String.format("상품 원가는 가격 변동폭보다 커야 합니다. 상품 원가: %d, 가격 변동폭: %d", originPrice, variationWidth),
                    ErrorCode.A009);
        }
    }

}

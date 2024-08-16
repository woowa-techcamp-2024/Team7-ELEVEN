package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class PercentagePricePolicy implements PricePolicy {

    private static final double MINIMUM_RATE = 0.0;
    private static final double MAXIMUM_RATE = 50.0;

    private final double discountRate;

    public PercentagePricePolicy(double discountRate) {
        validateDiscountRate(discountRate);
        this.discountRate = discountRate;
    }

    private void validateDiscountRate(double discountRate) {
        if (discountRate <= MINIMUM_RATE || discountRate > MAXIMUM_RATE) {
            String message = String.format("할인율은 %d%% 초과 %d%% 이하여야 합니다. 할인율: %f%%", (int) MINIMUM_RATE,
                    (int) MAXIMUM_RATE,
                    discountRate);
            throw new BadRequestException(message, ErrorCode.A010);
        }
    }

    @Override
    public long calculatePriceAtVariation(long price, long variationCount) {
        long discountedPrice = price;
        double discountFactor = (100 - discountRate) / 100.0;

        for (int i = 0; i < variationCount; i++) {
            discountedPrice = (long) Math.floor(discountedPrice * discountFactor);
        }

        return discountedPrice;
    }
}

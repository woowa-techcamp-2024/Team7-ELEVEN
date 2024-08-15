package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.ZonedDateTime;
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
    public void validate(ZonedDateTime startedAt, ZonedDateTime finishedAt, Duration variationDuration, long price) {
        Duration totalDuration = Duration.between(startedAt, finishedAt);
        int numberOfVariations = (int) totalDuration.dividedBy(variationDuration);

        long discountedPrice = price;
        double discountFactor = (100 - discountRate) / 100.0;

        for (int i = 0; i < numberOfVariations - 1; i++) {
            discountedPrice = (long) Math.floor(discountedPrice * discountFactor);
            if (discountedPrice <= 0) {
                String message = String.format("경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 할인횟수: %d, 할인율: %d%%", price,
                        numberOfVariations - 1, (int) discountRate);
                throw new BadRequestException(message, ErrorCode.A029);
            }
        }
    }
}

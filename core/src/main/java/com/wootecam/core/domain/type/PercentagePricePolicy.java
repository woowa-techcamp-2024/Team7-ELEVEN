package com.wootecam.core.domain.type;

import com.wootecam.core.exception.BadRequestException;
import com.wootecam.core.exception.ErrorCode;
import java.util.Objects;
import lombok.Getter;

@Getter
public class PercentagePricePolicy implements PricePolicy {

    private static final double MINIMUM_RATE = 0.0;
    private static final double MAXIMUM_RATE = 50.0;

    private final PricePolicyType type = PricePolicyType.PERCENTAGE;
    private double discountRate;

    public PercentagePricePolicy() {
    }

    public PercentagePricePolicy(double discountRate) {
        validateDiscountRate(discountRate);
        this.discountRate = discountRate;
    }

    private void validateDiscountRate(double discountRate) {
        if (discountRate <= MINIMUM_RATE || discountRate > MAXIMUM_RATE) {
            String message = String.format("할인율은 %d%% 초과 %d%% 이하여야 합니다. 할인율: %f%%", (int) MINIMUM_RATE,
                    (int) MAXIMUM_RATE,
                    discountRate);
            throw new BadRequestException(message, ErrorCode.A009);
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

    @Override
    public PricePolicyType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PercentagePricePolicy that = (PercentagePricePolicy) o;
        return Double.compare(discountRate, that.discountRate) == 0 && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, discountRate);
    }
}

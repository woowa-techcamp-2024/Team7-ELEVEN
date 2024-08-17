package com.wootecam.luckyvickyauction.core.auction.domain;

public interface PricePolicy {

    long calculatePriceAtVariation(long price, long variationCount);

    static PricePolicy createPercentagePricePolicy(int discountRate) {
        return new PercentagePricePolicy(discountRate);
    }

    static PricePolicy createConstantPricePolicy(int variationWidth) {
        return new ConstantPricePolicy(variationWidth);
    }

    PricePolicyType getType();
}

package com.wootecam.luckyvickyauction.core.auction.domain;

public interface PricePolicy {

    long applyWholeDiscount(long variationCount, long price);

    static PricePolicy createPercentagePricePolicy(int discountRate) {
        return new PercentagePricePolicy(discountRate);
    }

    static PricePolicy createConstantPricePolicy(int variationWidth) {
        return new ConstantPricePolicy(variationWidth);
    }
}

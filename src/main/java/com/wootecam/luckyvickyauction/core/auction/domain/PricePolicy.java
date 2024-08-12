package com.wootecam.luckyvickyauction.core.auction.domain;

public interface PricePolicy {

    void validate(long price);

    static PricePolicy percentagePricePolicy(int discountRate) {
        return new PercentagePricePolicy(discountRate);
    }

    static PricePolicy constantPricePolicy(int variationWidth) {
        return new ConstantPricePolicy(variationWidth);
    }
}

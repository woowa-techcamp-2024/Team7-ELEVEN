package com.wootecam.luckyvickyauction.core.auction.domain;

import java.time.Duration;
import java.time.ZonedDateTime;

public interface PricePolicy {

    void validate(ZonedDateTime startedAt, ZonedDateTime finishedAt, Duration variationDuration, long price);

    static PricePolicy createPercentagePricePolicy(int discountRate) {
        return new PercentagePricePolicy(discountRate);
    }

    static PricePolicy createConstantPricePolicy(int variationWidth) {
        return new ConstantPricePolicy(variationWidth);
    }
}

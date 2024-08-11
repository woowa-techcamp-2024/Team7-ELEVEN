package com.wootecam.luckyvickyauction.core.auction.domain;

public interface PricePolicy {

    void validate(long price);
}

package com.wootecam.luckyvickyauction.core.payment.domain;

import lombok.Getter;

@Getter
public enum BidStatus {

    BID("입찰"),
    REFUND("환불");

    private final String description;

    BidStatus(String description) {
        this.description = description;
    }
}

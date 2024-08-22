package com.wootecam.luckyvickyauction.core.payment.domain;

import lombok.Getter;

@Getter
public enum ReceiptStatus {

    PURCHASED("구매완료"),
    REFUND("환불완료");

    private final String description;

    ReceiptStatus(String description) {
        this.description = description;
    }
}

package com.wootecam.luckyvickyauction.domain.entity.type;

import lombok.Getter;

@Getter
public enum ReceiptStatus {

    PURCHASED("구매완료"),
    REFUND("환불완료"),
    FAILED("구매실패");

    private final String description;

    ReceiptStatus(String description) {
        this.description = description;
    }
}

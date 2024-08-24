package com.wootecam.luckyvickyauction.global.dto;

import java.time.LocalDateTime;

public record AuctionPurchaseRequestMessage(
        String requestId,
        Long buyerId,
        Long auctionId,
        Long price,
        Long quantity,
        LocalDateTime requestTime
) {
}

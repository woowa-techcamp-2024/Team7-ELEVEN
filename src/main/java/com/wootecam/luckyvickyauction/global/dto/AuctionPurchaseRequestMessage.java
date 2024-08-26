package com.wootecam.luckyvickyauction.global.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record AuctionPurchaseRequestMessage(
        UUID requestId,
        Long buyerId,
        Long auctionId,
        Long price,
        Long quantity,
        LocalDateTime requestTime
) {
}

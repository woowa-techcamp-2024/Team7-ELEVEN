package com.wootecam.luckyvickyauction.dto.auction.message;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AuctionPurchaseRequestMessage(
        String requestId,
        Long buyerId,
        Long auctionId,
        Long price,
        Long quantity,
        LocalDateTime requestTime
) {
}

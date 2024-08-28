package com.wootecam.luckyvickyauction.dto.auction.message;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class AuctionPurchaseRequestMessage {

    private String requestId;
    private Long buyerId;
    private Long auctionId;
    private Long price;
    private Long quantity;
    private LocalDateTime requestTime;

    public AuctionPurchaseRequestMessage() {
    }

    @Builder
    public AuctionPurchaseRequestMessage(
            String requestId,
            Long buyerId,
            Long auctionId,
            Long price,
            Long quantity,
            LocalDateTime requestTime
    ) {
        this.requestId = requestId;
        this.buyerId = buyerId;
        this.auctionId = auctionId;
        this.price = price;
        this.quantity = quantity;
        this.requestTime = requestTime;
    }
}

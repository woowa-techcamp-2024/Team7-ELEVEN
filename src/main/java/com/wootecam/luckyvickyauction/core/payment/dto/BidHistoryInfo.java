package com.wootecam.luckyvickyauction.core.payment.dto;

import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BidHistoryInfo(
        long bidHistoryId,
        String productName,
        long price,
        long quantity,
        BidStatus bidStatus,
        long auctionId,
        long sellerId,
        long buyerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

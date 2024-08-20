package com.wootecam.luckyvickyauction.core.payment.dto;

import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReceiptInfo(
        long receiptId,
        String productName,
        long price,
        long quantity,
        ReceiptStatus receiptStatus,
        long auctionId,
        long sellerId,
        long buyerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

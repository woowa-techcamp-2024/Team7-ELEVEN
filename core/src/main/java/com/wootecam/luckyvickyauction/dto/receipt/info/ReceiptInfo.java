package com.wootecam.luckyvickyauction.dto.receipt.info;

import com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReceiptInfo(
        String receiptId,
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

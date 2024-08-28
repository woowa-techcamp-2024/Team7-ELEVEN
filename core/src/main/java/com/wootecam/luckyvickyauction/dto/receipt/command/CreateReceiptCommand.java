package com.wootecam.luckyvickyauction.dto.receipt.command;

import com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus;
import lombok.Builder;

@Builder
public record CreateReceiptCommand(
        String requestId,
        Long auctionId,
        Long buyerId,
        Long price,
        Long quantity,
        ReceiptStatus status
) {
}

package com.wootecam.luckyvickyauction.global.dto;

import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionRefundRequestMessage(
        SignInInfo buyerInfo,
        UUID receiptId,
        LocalDateTime requestTime
) {
}

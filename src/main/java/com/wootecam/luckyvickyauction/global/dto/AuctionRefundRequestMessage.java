package com.wootecam.luckyvickyauction.global.dto;

import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.LocalDateTime;

public record AuctionRefundRequestMessage(
        SignInInfo buyerInfo,
        Long receiptId,
        LocalDateTime requestTime
) {
}

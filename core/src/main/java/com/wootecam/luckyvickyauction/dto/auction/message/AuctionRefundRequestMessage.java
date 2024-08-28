package com.wootecam.luckyvickyauction.dto.auction.message;

import com.wootecam.luckyvickyauction.dto.member.info.SignInInfo;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionRefundRequestMessage(
        SignInInfo buyerInfo,
        UUID receiptId,
        LocalDateTime requestTime
) {
}

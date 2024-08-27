package com.wootecam.core.dto.auction.message;

import com.wootecam.core.dto.member.info.SignInInfo;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionRefundRequestMessage(
        SignInInfo buyerInfo,
        UUID receiptId,
        LocalDateTime requestTime
) {
}

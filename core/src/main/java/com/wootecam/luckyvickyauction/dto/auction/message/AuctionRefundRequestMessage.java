package com.wootecam.luckyvickyauction.dto.auction.message;

import com.wootecam.luckyvickyauction.dto.member.info.SignInInfo;
import java.time.LocalDateTime;

public record AuctionRefundRequestMessage(
        SignInInfo buyerInfo,
        String receiptId,
        LocalDateTime requestTime
) {
}

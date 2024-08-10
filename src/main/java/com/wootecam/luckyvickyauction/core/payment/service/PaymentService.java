package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.domain.Buyer;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentService {
    private final AuctionService auctionService;

    public void submitBid(Member signInMember, long auctionId, int quantity) {
        if (!signInMember.isBuyer()) {
            throw new BadRequestException("구매자만 입찰을 할 수 있습니다.", ErrorCode.P000);
        }

        AuctionInfo auctionInfo = auctionService.getAuction(auctionId);
        if (auctionInfo.quantity() < quantity) {
            throw new BadRequestException("남아있는 재고보다 더 많은 입찰은 불가능합니다.", ErrorCode.P001);
        }

        Buyer signInBuyer = (Buyer) signInMember;
        if (signInBuyer.canBuy(auctionInfo.price(), quantity)) {
            throw new BadRequestException("포인트가 부족합니다.", ErrorCode.P002);
        }
    }
}

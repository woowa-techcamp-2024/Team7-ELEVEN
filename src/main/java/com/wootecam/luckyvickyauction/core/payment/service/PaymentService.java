package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentService {
    private final AuctionService auctionService;
    private final MemberRepository memberRepository;
    private final BidHistoryRepository bidHistoryRepository;

    public void submitBid(Member signInMember, long auctionId, int quantity) {
        if (!signInMember.isBuyer()) {
            throw new BadRequestException("구매자만 입찰을 할 수 있습니다.", ErrorCode.P000);
        }

        AuctionInfo auctionInfo = auctionService.getAuction(auctionId);
        if (auctionInfo.quantity() < quantity) {
            throw new BadRequestException("남아있는 재고보다 더 많은 입찰은 불가능합니다.", ErrorCode.P001);
        }

        // TODO 입찰 내역 도메인 설계 필요 -> id값을 가질건지, 도메인을 가질건지
        signInMember.usePoint(auctionInfo.price() * quantity);

        BidHistory bidHistory = new BidHistory();
        bidHistoryRepository.save(bidHistory);
        memberRepository.save(signInMember);
    }
}

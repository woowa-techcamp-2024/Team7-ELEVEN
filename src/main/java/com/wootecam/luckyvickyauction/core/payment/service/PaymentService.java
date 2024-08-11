package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentService {
    private final AuctionService auctionService;
    private final MemberRepository memberRepository;
    private final BidHistoryRepository bidHistoryRepository;

    /**
     *   1. 구매자 확인 <br>
     *   2. 구매자 포인트를 감소 <br>
     *   3. 판매자에게 포인트 지급 <br>
     *   4. 구매 요청 <br>
     *     - 실패하면 -> 예외 발생 및 구매자와 판매자 포인트 롤백 <br>
     *     - 성공하면 -> BidHistory 저장 및 구매자, 판매자 업데이트 적용
     */
    public void process(Member buyer, long price, long auctionId, long quantity) {
        if (!buyer.isBuyer()) {
            throw new BadRequestException("구매자만 입찰을 할 수 있습니다.", ErrorCode.P000);
        }
        AuctionInfo auctionInfo = auctionService.getAuction(auctionId);
        Member seller = findMemberObject(auctionInfo.sellerId());
        buyer.usePoint(price * quantity);
        seller.chargePoint(price * quantity);

        submitBid(price, auctionId, quantity, buyer, seller);

        Member savedBuyer = memberRepository.save(buyer);
        Member savedSeller = memberRepository.save(seller);
        BidHistory bidHistory = BidHistory.builder()
                .productName(auctionInfo.productName())
                .price(price)
                .quantity(quantity)
                .seller(savedSeller)
                .buyer(savedBuyer)
                .build();
        bidHistoryRepository.save(bidHistory);
    }

    private Member findMemberObject(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.M002));
    }

    private void submitBid(long price, long auctionId, long quantity, Member buyer, Member seller) {
        try {
            auctionService.submitBid(auctionId, price, quantity);
        } catch (BadRequestException e) {
            buyer.chargePoint(price * quantity);
            seller.usePoint(price * quantity);
        }
    }
}

package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
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
     * 1. 구매자 확인 <br> 2. 구매자 포인트를 감소 <br> 3. 판매자에게 포인트 지급 <br> 4. 구매 요청 <br> - 실패하면 -> 예외 발생 및 구매자와 판매자 포인트 롤백 <br> -
     * 성공하면 -> BidHistory 저장 및 구매자, 판매자 업데이트 적용
     */
    public void process(Member buyer, long price, long auctionId, long quantity) {
        if (!buyer.isBuyer()) {
            throw new BadRequestException("구매자만 입찰을 할 수 있습니다.", ErrorCode.P000);
        }
        AuctionInfo auctionInfo = auctionService.getAuction(auctionId);
        Member seller = findMemberObject(auctionInfo.sellerId());
        buyer.usePoint(price * quantity);
        seller.chargePoint(price * quantity);

        if (submitBid(price, auctionId, quantity, buyer, seller)) {
            Member savedBuyer = memberRepository.save(buyer);
            Member savedSeller = memberRepository.save(seller);
            BidHistory bidHistory = BidHistory.builder()
                    .productName(auctionInfo.productName())
                    .price(price)
                    .quantity(quantity)
                    .bidStatus(BidStatus.BID)
                    .seller(savedSeller)
                    .buyer(savedBuyer)
                    .build();
            bidHistoryRepository.save(bidHistory);
        }
    }

    private Member findMemberObject(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.M002));
    }

    private boolean submitBid(long price, long auctionId, long quantity, Member buyer, Member seller) {
        try {
            auctionService.submitBid(auctionId, price, quantity);
            return true;
        } catch (BadRequestException e) {
            buyer.chargePoint(price * quantity);
            seller.usePoint(price * quantity);
            return false;
        }
    }

    /**
     * 구매자는 자신의 입찰 내역에서 상품을 환불할 수 있다 <br> 1. 환불을 요청한 사용자가 구매자 권한이 맞는 지 확인한다 <br> 2. 환불할 입찰 내역의 구매자가 환불을 요청한 사용자인지 확인한다
     * <br> 3. 환불 금액과 수량을 받아와서 교환한다 <br> 4. 경매 서비스에 환불 요청한다 <br> 5. 환불 이후 정보들을 저장한다
     *
     * @param buyer        환불을 요청한 사용자
     * @param bidHistoryId 환불할 입찰 내역의 id
     */
    public void refund(Member buyer, long bidHistoryId) {
        if (!buyer.isBuyer()) {
            throw new BadRequestException("구매자만 환불을 할 수 있습니다.", ErrorCode.P000);
        }

        BidHistory refundTargetBidHistory = findRefundTargetBidHistory(bidHistoryId);
        if (refundTargetBidHistory.isRefundStatus()) {
            throw new BadRequestException("이미 환불된 입찰 내역입니다.", ErrorCode.P003);
        }

        Member refundTargetBuyer = refundTargetBidHistory.getBuyer();
        if (!buyer.isSameMember(refundTargetBuyer.getSignInId())) {
            throw new BadRequestException("환불할 입찰 내역의 구매자만 환불을 할 수 있습니다.", ErrorCode.P004);
        }

        long price = refundTargetBidHistory.getPrice();
        long quantity = refundTargetBidHistory.getQuantity();

        Member seller = refundTargetBidHistory.getSeller();
        buyer.chargePoint(price * quantity);
        seller.usePoint(price * quantity);

        // 경매 서비스에 환불 요청
        auctionService.cancelBid(refundTargetBidHistory.getAuctionId(), quantity);

        // 환불 요청에 대한 정보 저장
        Member savedBuyer = memberRepository.save(buyer);
        Member savedSeller = memberRepository.save(seller);
        bidHistoryRepository.save(BidHistory.builder()
                .productName(refundTargetBidHistory.getProductName())
                .price(price)
                .quantity(quantity)
                .bidStatus(BidStatus.REFUND)
                .seller(savedSeller)
                .buyer(savedBuyer)
                .build());
    }

    private BidHistory findRefundTargetBidHistory(long bidHistoryId) {
        return bidHistoryRepository.findById(bidHistoryId).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역을 찾을 수 없습니다. 내역 id=" + bidHistoryId, ErrorCode.P002));
    }

    /**
     * 사용자의 포인트를 충전한다
     * - 포인트가 음수이면 예외가 발생한다
     * @param member        포인트를 충전할 사용자
     * @param chargePoint   충전할 포인트
     */
    public void chargePoint(Member member, long chargePoint) {
        if (chargePoint < 0) {
            throw new BadRequestException("포인트는 음수가 될 수 없습니다. 충전 포인트=" + chargePoint, ErrorCode.P005);
        }

        member.chargePoint(chargePoint);
        memberRepository.save(member);
    }
}

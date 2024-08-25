package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.Receipt;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptStatus;
import com.wootecam.luckyvickyauction.global.dto.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.global.dto.AuctionRefundRequestMessage;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
@RequiredArgsConstructor
public class BasicAuctioneer implements Auctioneer {

    private final AuctionService auctionService;
    private final MemberRepository memberRepository;
    private final ReceiptRepository receiptRepository;

    /**
     * 1. 구매자 확인 <br> 2. 구매자 포인트를 감소 <br> 3. 판매자에게 포인트 지급 <br> 4. 구매 요청 <br> - 실패하면 -> 예외 발생 및 구매자와 판매자 포인트 롤백 <br> -
     * 성공하면 -> Receipt 저장 및 구매자, 판매자 업데이트 적용
     */
    @Transactional
    public void process(AuctionPurchaseRequestMessage message) {
        Member buyer = findMemberObject(message.buyerId());
        AuctionInfo auctionInfo = auctionService.getAuction(message.auctionId());
        Member seller = findMemberObject(auctionInfo.sellerId());
        buyer.usePoint(message.price() * message.quantity());
        seller.chargePoint(message.price() * message.quantity());

        auctionService.submitPurchase(message.auctionId(), message.price(), message.quantity(), message.requestTime());
        Member savedBuyer = memberRepository.save(buyer);
        Member savedSeller = memberRepository.save(seller);
        Receipt receipt = Receipt.builder()
                .productName(auctionInfo.productName())
                .price(message.price())
                .quantity(message.quantity())
                .receiptStatus(ReceiptStatus.PURCHASED)
                .sellerId(savedSeller.getId())
                .buyerId(savedBuyer.getId())
                .auctionId(message.auctionId())
                .build();
        receiptRepository.save(receipt);
    }

    private Member findMemberObject(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. id=" + id, ErrorCode.M002));
    }

    @Override
    public void refund(AuctionRefundRequestMessage message) {

    }
}

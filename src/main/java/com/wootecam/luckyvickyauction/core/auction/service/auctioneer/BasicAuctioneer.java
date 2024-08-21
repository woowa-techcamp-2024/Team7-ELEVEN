package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.domain.Receipt;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptStatus;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuctioneer implements Auctioneer {

    private final AuctionService auctionService;
    private final MemberRepository memberRepository;
    private final ReceiptRepository receiptRepository;

    @Transactional
    public void process(SignInInfo buyerInfo, long price, long auctionId, long quantity, LocalDateTime requestTime) {
        Member buyer = findMemberObject(buyerInfo.id());
        AuctionInfo auctionInfo = auctionService.getAuction(auctionId);
        Member seller = findMemberObject(auctionInfo.sellerId());
        buyer.usePoint(price * quantity);
        seller.chargePoint(price * quantity);

        auctionService.submitPurchase(auctionId, price, quantity, requestTime);
        Member savedBuyer = memberRepository.save(buyer);
        Member savedSeller = memberRepository.save(seller);
        Receipt receipt = Receipt.builder()
                .productName(auctionInfo.productName())
                .price(price)
                .quantity(quantity)
                .receiptStatus(ReceiptStatus.PURCHASED)
                .sellerId(savedSeller.getId())
                .buyerId(savedBuyer.getId())
                .build();
        receiptRepository.save(receipt);
    }

    private Member findMemberObject(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. id=" + id, ErrorCode.M002));
    }
}

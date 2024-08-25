package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.Receipt;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptStatus;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import com.wootecam.luckyvickyauction.global.aop.DistributedLock;
import com.wootecam.luckyvickyauction.global.dto.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.global.dto.AuctionRefundRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
@RequiredArgsConstructor
public class BasicAuctioneer implements Auctioneer {

    private final AuctionService auctionService;
    private final PaymentService paymentService;
    private final ReceiptRepository receiptRepository;
    private final MemberRepository memberRepository;

    /**
     * 1. 구매자 확인 <br> 2. 구매자 포인트를 감소 <br> 3. 판매자에게 포인트 지급 <br> 4. 구매 요청 <br> - 실패하면 -> 예외 발생 및 구매자와 판매자 포인트 롤백 <br> -
     * 성공하면 -> Receipt 저장 및 구매자, 판매자 업데이트 적용
     */
    @Override
    @Transactional
    @DistributedLock("#auctionId + ':auction:lock'")
    public void process(AuctionPurchaseRequestMessage message) {
        AuctionInfo auctionInfo = auctionService.getAuction(message.auctionId());
        auctionService.submitPurchase(message.auctionId(), message.price(), message.quantity(), message.requestTime());

        long buyerId = message.buyerId();
        long sellerId = auctionInfo.sellerId();
        paymentService.pointTransfer(buyerId, sellerId, message.price() * message.quantity());

        Receipt receipt = Receipt.builder()
                .productName(auctionInfo.productName())
                .price(message.price())
                .quantity(message.quantity())
                .receiptStatus(ReceiptStatus.PURCHASED)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .auctionId(message.auctionId())
                .build();
        receiptRepository.save(receipt);
    }

    @Override
    public void refund(AuctionRefundRequestMessage message) {

    }

}

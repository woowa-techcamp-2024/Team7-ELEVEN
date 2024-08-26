package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.core.domain.Receipt;
import com.wootecam.core.domain.type.ReceiptStatus;
import com.wootecam.core.domain.type.Role;
import com.wootecam.core.dto.auction.info.AuctionInfo;
import com.wootecam.core.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.core.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.exception.AuthorizationException;
import com.wootecam.core.exception.BadRequestException;
import com.wootecam.core.exception.ErrorCode;
import com.wootecam.core.exception.NotFoundException;
import com.wootecam.core.repository.ReceiptRepository;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import com.wootecam.luckyvickyauction.global.aop.DistributedLock;
import java.time.LocalDateTime;
import java.util.UUID;
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

    /**
     * 1. 구매자 확인 <br> 2. 구매자 포인트를 감소 <br> 3. 판매자에게 포인트 지급 <br> 4. 구매 요청 <br> - 실패하면 -> 예외 발생 및 구매자와 판매자 포인트 롤백 <br> -
     * 성공하면 -> Receipt 저장 및 구매자, 판매자 업데이트 적용
     */
    @Override
    @Transactional
    @DistributedLock("#message.auctionId + ':auction:lock'")
    public void process(AuctionPurchaseRequestMessage message) {
        AuctionInfo auctionInfo = auctionService.getAuction(message.auctionId());
        auctionService.submitPurchase(message.auctionId(), message.price(), message.quantity(), message.requestTime());

        long buyerId = message.buyerId();
        long sellerId = auctionInfo.sellerId();
        paymentService.pointTransfer(buyerId, sellerId, message.price() * message.quantity());

        Receipt receipt = Receipt.builder()
                .id(message.requestId())
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

    /**
     * 경매 환불을 진행한다.
     *
     * @param message
     */
    @Override
    @Transactional
    @DistributedLock("#message.receiptId + ':receipt:lock'")
    public void refund(AuctionRefundRequestMessage message) {
        verifyHasBuyerRole(message.buyerInfo());

        Receipt receipt = findRefundTargetReceipt(message.receiptId());
        verifyEndAuction(message.requestTime(), receipt.getAuctionId());
        verifySameBuyer(message.buyerInfo(), receipt.getBuyerId());
        receipt.markAsRefund();

        paymentService.pointTransfer(receipt.getSellerId(), receipt.getBuyerId(),
                receipt.getPrice() * receipt.getQuantity());
        auctionService.cancelPurchase(receipt.getAuctionId(), receipt.getQuantity());

        receiptRepository.save(receipt);  // 정상적으로 환불 처리된 경우 해당 이력을 '환불' 상태로 변경
    }

    private void verifyHasBuyerRole(SignInInfo buyerInfo) {
        if (!buyerInfo.isType(Role.BUYER)) {
            throw new AuthorizationException("구매자만 환불을 할 수 있습니다.", ErrorCode.P000);
        }
    }

    private void verifyEndAuction(LocalDateTime requestTime, long auctionId) {
        AuctionInfo auction = auctionService.getAuction(auctionId);

        if (requestTime.isBefore(auction.finishedAt())) {
            throw new BadRequestException("종료된 경매만 환불할 수 있습니다.", ErrorCode.P007);
        }
    }

    private void verifySameBuyer(SignInInfo buyerInfo, long receiptBuyerId) {
        if (buyerInfo.id() != receiptBuyerId) {
            throw new AuthorizationException("환불할 입찰 내역의 구매자만 환불을 할 수 있습니다.", ErrorCode.P004);
        }
    }

    private Receipt findRefundTargetReceipt(UUID receiptId) {
        return receiptRepository.findById(receiptId).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역을 찾을 수 없습니다. 내역 id=" + receiptId, ErrorCode.P002));
    }

}

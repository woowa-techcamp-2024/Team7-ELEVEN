package com.wootecam.luckyvickyauction.service;

import com.wootecam.luckyvickyauction.aop.DistributedLock;
import com.wootecam.luckyvickyauction.domain.entity.Receipt;
import com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus;
import com.wootecam.luckyvickyauction.domain.entity.type.Role;
import com.wootecam.luckyvickyauction.domain.repository.ReceiptRepository;
import com.wootecam.luckyvickyauction.dto.auction.info.AuctionInfo;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.luckyvickyauction.dto.member.info.SignInInfo;
import com.wootecam.luckyvickyauction.exception.AuthorizationException;
import com.wootecam.luckyvickyauction.exception.BadRequestException;
import com.wootecam.luckyvickyauction.exception.ErrorCode;
import com.wootecam.luckyvickyauction.exception.NotFoundException;
import com.wootecam.luckyvickyauction.service.auction.AuctionService;
import com.wootecam.luckyvickyauction.service.auctioneer.Auctioneer;
import com.wootecam.luckyvickyauction.service.payment.PaymentService;
import java.time.LocalDateTime;
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
    public void process(AuctionPurchaseRequestMessage message, Runnable... postProcesses) {
        AuctionInfo auctionInfo = auctionService.getAuction(message.getAuctionId());
        auctionService.submitPurchase(message.getAuctionId(), message.getPrice(), message.getQuantity(),
                message.getRequestTime());

        long buyerId = message.getBuyerId();
        long sellerId = auctionInfo.sellerId();
        paymentService.pointTransfer(buyerId, sellerId, message.getPrice() * message.getQuantity());

        Receipt receipt = Receipt.builder()
                .id(message.getRequestId())
                .productName(auctionInfo.productName())
                .price(message.getPrice())
                .quantity(message.getQuantity())
                .receiptStatus(ReceiptStatus.PURCHASED)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .auctionId(message.getAuctionId())
                .build();
        receiptRepository.save(receipt);

        for (Runnable postProcess : postProcesses) {
            postProcess.run();
        }
    }

    /**
     * 경매 환불을 진행한다.
     *
     * @param message
     */
    @Override
    @Transactional
    public void refund(AuctionRefundRequestMessage message, Runnable... postProcesses) {
        verifyHasBuyerRole(message.buyerInfo());

        Receipt receipt = findRefundTargetReceiptForUpdate(message.receiptId());
        verifySameBuyer(message.buyerInfo(), receipt.getBuyerId());
        receipt.markAsRefund();

        AuctionInfo auction = auctionService.getAuctionForUpdate(receipt.getAuctionId());
        verifyEndAuction(message.requestTime(), auction.finishedAt());

        auctionService.cancelPurchase(receipt.getAuctionId(), receipt.getQuantity());
        paymentService.pointTransfer(receipt.getSellerId(), receipt.getBuyerId(),
                receipt.getPrice() * receipt.getQuantity());

        receiptRepository.save(receipt);

        for (Runnable postProcess : postProcesses) {
            postProcess.run();
        }
    }

    private void verifyHasBuyerRole(SignInInfo buyerInfo) {
        if (!buyerInfo.isType(Role.BUYER)) {
            throw new AuthorizationException("구매자만 환불을 할 수 있습니다.", ErrorCode.P000);
        }
    }


    private void verifyEndAuction(LocalDateTime requestTime, LocalDateTime auctionFinishedAt) {
        if (requestTime.isBefore(auctionFinishedAt)) {
            throw new BadRequestException("종료된 경매만 환불할 수 있습니다.", ErrorCode.P007);
        }
    }

    private void verifySameBuyer(SignInInfo buyerInfo, long receiptBuyerId) {
        if (buyerInfo.id() != receiptBuyerId) {
            throw new AuthorizationException("환불할 입찰 내역의 구매자만 환불을 할 수 있습니다.", ErrorCode.P004);
        }
    }

    private Receipt findRefundTargetReceiptForUpdate(String receiptId) {
        return receiptRepository.findByIdForUpdate(receiptId).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역을 찾을 수 없습니다. 내역 id=" + receiptId, ErrorCode.P002));
    }
}

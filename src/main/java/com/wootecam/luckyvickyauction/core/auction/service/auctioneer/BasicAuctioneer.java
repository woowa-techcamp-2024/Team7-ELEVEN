package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.domain.Receipt;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptStatus;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import com.wootecam.luckyvickyauction.global.aop.DistributedLock;
import com.wootecam.luckyvickyauction.global.dto.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.global.dto.AuctionRefundRequestMessage;
import com.wootecam.luckyvickyauction.global.exception.AuthorizationException;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
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

    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public void refund(AuctionRefundRequestMessage message) {
        verifyHasBuyerRole(message.buyerInfo());

        Receipt refundTargetReceipt = findRefundTargetReceipt(message.receiptId());
        verifyEndAuction(message.requestTime(), refundTargetReceipt.getAuctionId());

        refundTargetReceipt.markAsRefund();

        verifySameBuyer(message.buyerInfo(), refundTargetReceipt.getBuyerId());

        // TODO 동작 잘되는지 확인하고 이거 주석풀기
//        paymentService.pointTransfer(refundTargetReceipt.getSellerId(), refundTargetReceipt.getBuyerId(), refundTargetReceipt.getPrice() * refundTargetReceipt.getQuantity());
        ///////////////////////////////////////
        long price = refundTargetReceipt.getPrice();
        long quantity = refundTargetReceipt.getQuantity();

        Member buyer = memberRepository.findById(message.buyerInfo().id()).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역의 구매자를 찾을 수 없습니다. 구매자 id=" + message.buyerInfo().id(),
                        ErrorCode.M002));
        Member seller = memberRepository.findById(refundTargetReceipt.getSellerId()).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역의 판매자를 찾을 수 없습니다. 판매자 id=" + refundTargetReceipt.getSellerId(),
                        ErrorCode.M002));

        buyer.chargePoint(price * quantity);
        seller.usePoint(price * quantity);

        memberRepository.save(buyer);
        memberRepository.save(seller);
        ////////////////////////////////////////

        auctionService.cancelPurchase(refundTargetReceipt.getAuctionId(), quantity);

        receiptRepository.save(refundTargetReceipt);  // 정상적으로 환불 처리된 경우 해당 이력을 '환불' 상태로 변경
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
        if (!(buyerInfo.id() == receiptBuyerId)) {
            throw new AuthorizationException("환불할 입찰 내역의 구매자만 환불을 할 수 있습니다.", ErrorCode.P004);
        }
    }

    private Receipt findRefundTargetReceipt(long receiptId) {
        return receiptRepository.findById(receiptId).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역을 찾을 수 없습니다. 내역 id=" + receiptId, ErrorCode.P002));
    }

    private Member findMemberObject(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. id=" + id, ErrorCode.M002));
    }

}

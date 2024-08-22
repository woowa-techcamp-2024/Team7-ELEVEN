package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.domain.Receipt;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptStatus;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final AuctionService auctionService;
    private final MemberRepository memberRepository;
    private final ReceiptRepository receiptRepository;

    /**
     * 1. 구매자 확인 <br> 2. 구매자 포인트를 감소 <br> 3. 판매자에게 포인트 지급 <br> 4. 구매 요청 <br> - 실패하면 -> 예외 발생 및 구매자와 판매자 포인트 롤백 <br> -
     * 성공하면 -> Receipt 저장 및 구매자, 판매자 업데이트 적용
     */
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

    /**
     * 구매자는 자신의 입찰 내역에서 상품을 환불할 수 있다 <br> 1. 환불을 요청한 사용자가 구매자 권한이 맞는 지 확인한다 <br> 2. 환불할 입찰 내역의 구매자가 환불을 요청한 사용자인지 확인한다
     * <br> 3. 환불 금액과 수량을 받아와서 교환한다 <br> 4. 경매 서비스에 환불 요청한다 <br> 5. 환불 이후 정보들을 저장한다
     *
     * @param buyerInfo 환불을 요청한 사용자
     * @param receiptId 환불할 거래 내역의 id
     */
    @Transactional
    public void refund(SignInInfo buyerInfo, long receiptId) {
        if (!buyerInfo.isType(Role.BUYER)) {
            throw new UnauthorizedException("구매자만 환불을 할 수 있습니다.", ErrorCode.P000);
        }

        Receipt refundTargetReceipt = findRefundTargetReceipt(receiptId);
        refundTargetReceipt.markAsRefund();

        if (!(buyerInfo.id() == refundTargetReceipt.getBuyerId())) {
            throw new UnauthorizedException("환불할 입찰 내역의 구매자만 환불을 할 수 있습니다.", ErrorCode.P004);
        }

        // 사용자 포인트 변경
        long price = refundTargetReceipt.getPrice();
        long quantity = refundTargetReceipt.getQuantity();

        Member buyer = memberRepository.findById(buyerInfo.id()).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역의 구매자를 찾을 수 없습니다. 구매자 id=" + buyerInfo.id(), ErrorCode.M002));
        Member seller = memberRepository.findById(refundTargetReceipt.getSellerId()).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역의 판매자를 찾을 수 없습니다. 판매자 id=" + refundTargetReceipt.getSellerId(),
                        ErrorCode.M002));

        buyer.chargePoint(price * quantity);
        seller.usePoint(price * quantity);

        auctionService.cancelPurchase(refundTargetReceipt.getAuctionId(), quantity);

        memberRepository.save(buyer);
        memberRepository.save(seller);
        receiptRepository.save(refundTargetReceipt);  // 정상적으로 환불 처리된 경우 해당 이력을 '환불' 상태로 변경
    }

    private Receipt findRefundTargetReceipt(long receiptId) {
        return receiptRepository.findById(receiptId).orElseThrow(
                () -> new NotFoundException("환불할 입찰 내역을 찾을 수 없습니다. 내역 id=" + receiptId, ErrorCode.P002));
    }

    /**
     * 사용자의 포인트를 충전한다 - 포인트가 음수이면 예외가 발생한다
     *
     * @param memberInfo  포인트를 충전할 사용자
     * @param chargePoint 충전할 포인트
     */
    @Transactional
    public void chargePoint(SignInInfo memberInfo, long chargePoint) {
        if (chargePoint < 0) {
            throw new BadRequestException("포인트는 음수가 될 수 없습니다. 충전 포인트=" + chargePoint, ErrorCode.P005);
        }
        Member member = memberRepository.findById(memberInfo.id())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. 사용자 id=" + memberInfo.id(), ErrorCode.M002));

        member.chargePoint(chargePoint);
        memberRepository.save(member);
    }
}

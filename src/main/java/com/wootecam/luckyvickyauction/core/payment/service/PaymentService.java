package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.domain.Receipt;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptRepository;
import com.wootecam.luckyvickyauction.global.aop.DistributedLock;
import com.wootecam.luckyvickyauction.global.exception.AuthorizationException;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AuctionService auctionService;
    private final MemberRepository memberRepository;
    private final ReceiptRepository receiptRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public void refund(SignInInfo buyerInfo, long receiptId, LocalDateTime requestTime) {
        verifyHasBuyerRole(buyerInfo);

        Receipt refundTargetReceipt = findRefundTargetReceipt(receiptId);
        verifyEndAuction(requestTime, refundTargetReceipt.getAuctionId());

        refundTargetReceipt.markAsRefund();

        verifySameBuyer(buyerInfo, refundTargetReceipt.getBuyerId());

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

    @Transactional
    public void chargePoint(SignInInfo memberInfo, long chargePoint) {
        if (chargePoint <= 0) {
            throw new BadRequestException("포인트는 0원 이하로 충전할 수 없습니다. 충전 포인트=" + chargePoint, ErrorCode.P005);
        }
        Member member = memberRepository.findById(memberInfo.id())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. 사용자 id=" + memberInfo.id(), ErrorCode.M002));

        member.chargePoint(chargePoint);
        memberRepository.save(member);
    }

    @Transactional
    @DistributedLock("#recipientId + ':point:lock'")
    public void pointTransfer(long senderId, long recipientId, long amount) {
        Member sender = findMemberObject(senderId);
        Member recipient = findMemberObject(recipientId);

        sender.pointTransfer(recipient, amount);
        log.debug("  - Member.{}의 포인트 {}원을 Member.{} 에게 전달합니다.", sender.getId(), amount, recipientId);
        log.debug("  - Member.{}의 잔고: {}, Member.{}의 잔고: {}", sender.getId(), sender.getPoint().getAmount(),
                recipientId, recipient.getPoint().getAmount());

        memberRepository.save(sender);
        memberRepository.save(recipient);
    }

    private Member findMemberObject(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. id=" + id, ErrorCode.M002));
    }
}

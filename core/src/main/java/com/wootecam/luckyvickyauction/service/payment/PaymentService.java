package com.wootecam.luckyvickyauction.service.payment;

import com.wootecam.luckyvickyauction.aop.DistributedLock;
import com.wootecam.luckyvickyauction.domain.entity.Member;
import com.wootecam.luckyvickyauction.domain.repository.MemberRepository;
import com.wootecam.luckyvickyauction.dto.member.info.SignInInfo;
import com.wootecam.luckyvickyauction.exception.BadRequestException;
import com.wootecam.luckyvickyauction.exception.ErrorCode;
import com.wootecam.luckyvickyauction.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MemberRepository memberRepository;

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

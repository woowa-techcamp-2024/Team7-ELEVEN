package com.wootecam.core.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.core.domain.entity.Auction;
import com.wootecam.core.domain.entity.Member;
import com.wootecam.core.domain.entity.Point;
import com.wootecam.core.domain.entity.Receipt;
import com.wootecam.core.domain.entity.type.ConstantPricePolicy;
import com.wootecam.core.domain.entity.type.ReceiptStatus;
import com.wootecam.core.domain.entity.type.Role;
import com.wootecam.core.domain.repository.AuctionRepository;
import com.wootecam.core.domain.repository.MemberRepository;
import com.wootecam.core.domain.repository.ReceiptRepository;
import com.wootecam.core.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.core.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.exception.AuthorizationException;
import com.wootecam.core.exception.BadRequestException;
import com.wootecam.core.exception.ErrorCode;
import com.wootecam.core.exception.NotFoundException;
import com.wootecam.core.service.auctioneer.Auctioneer;
import com.wootecam.test.context.ServiceTest;
import com.wootecam.test.fixture.AuctionFixture;
import com.wootecam.test.fixture.MemberFixture;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PaymentServiceTest extends ServiceTest {

    @Autowired
    public PaymentService paymentService;
    @Autowired
    public MemberRepository memberRepository;

    @Nested
    class chargePoint_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 포인트가_충전된다() {
                // given
                Member member = Member.builder()
                        .signInId("testSignInId")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(0))
                        .build();
                Member savedMember = memberRepository.save(member);

                // when
                paymentService.chargePoint(new SignInInfo(savedMember.getId(), Role.BUYER), 1000L);

                // then
                Member resultMember = memberRepository.findById(savedMember.getId()).get();
                Point point = resultMember.getPoint();
                assertThat(point.getAmount()).isEqualTo(1000L);
            }

        }

        @Nested
        class 만약_충전할_포인트가_음수라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member member = Member.createMemberWithRole("testSignInId", "password00", "BUYER");
                Member savedMember = memberRepository.save(member);

                // expect
                assertThatThrownBy(
                        () -> paymentService.chargePoint(new SignInInfo(savedMember.getId(), Role.BUYER), -1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("포인트는 0원 이하로 충전할 수 없습니다. 충전 포인트=-1")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.P005));
            }
        }
    }
}

package com.wootecam.luckyvickyauction.core.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.repository.FakeMemberRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// TODO: AuctionService 세부 사항 결정되면 테스트
class PaymentServiceTest {

    private MemberRepository memberRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        paymentService = new PaymentService(null, memberRepository, null);
    }

    @Nested
    class process_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 입찰이_진행된다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_요청한_사용자가_구매자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_판매자를_찾을_수_없다면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_요청한_물건의_금액이_사용자가_가진_포인트보다_많다면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }
    }

    @Nested
    class refund_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 환불이_진행된다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_요청한_사용자가_구매자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_환불할_입찰_내역을_찾을_수_없다면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_이미_환불된_입찰_내역이라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_입찰_내역의_구매자가_요청한_사용자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }
    }

    @Nested
    class chargePoint_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 포인트가_충전된다() {
                // given
                Member member = new Member(1L, "test", "test", Role.BUYER, new Point(0));

                // when
                paymentService.chargePoint(member, 1000L);

                // then
                Member savedMember = memberRepository.findById(1L).get();
                Point savedMemberPoint = savedMember.getPoint();
                assertThat(savedMemberPoint.getAmount()).isEqualTo(1000L);
            }
        }

        @Nested
        class 만약_충전할_포인트가_음수라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member member = new Member(1L, "test", "test", Role.BUYER, new Point(0));

                // expect
                assertThatThrownBy(() -> paymentService.chargePoint(member, -1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("포인트는 음수가 될 수 없습니다. 충전 포인트=-1")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.P005));
            }

        }
    }
}

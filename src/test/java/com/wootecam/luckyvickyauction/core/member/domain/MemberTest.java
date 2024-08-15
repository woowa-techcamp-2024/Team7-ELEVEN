package com.wootecam.luckyvickyauction.core.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void 회원을_생성할_수_있다() {
        // when
        Member member = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // then
        assertAll(
                () -> assertThat(member.getSignInId()).isEqualTo("testId"),
                () -> assertThat(member.getPassword()).isEqualTo("password00"),
                () -> assertThat(member.getRole()).isEqualTo(Role.BUYER),
                () -> assertThat(member.getPoint()).isEqualTo(new Point(100))
        );
    }

    @Nested
    class validatePassword_메소드는 extends ValidatePasswordTest {
    }

    @Test
    void usePoint_포인트를_사용할_수_있다() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();
        long price = 10L;
        long quantity = 10L;

        // when
        buyer.usePoint(price * quantity);

        // then
        assertThat(buyer.getPoint()).isEqualTo(new Point(0));
    }

    @Test
    void usePoint_보유한_포인트보다_많은_포인트를_사용하려하면_예외가_발생한다() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // expect
        assertThatThrownBy(() -> buyer.usePoint(10 * 11))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("포인트가 부족합니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.P001));
    }

    @Test
    void chargePoint_포인트를_충전할_수_있다() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // when
        buyer.chargePoint(100);

        // then
        assertThat(buyer.getPoint()).isEqualTo(new Point(200));
    }

    @Nested
    class confirmPassword_메소드는 {

        @Test
        void 비밀번호가_동일하면_true를_반환한다() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.confirmPassword("password00")).isTrue();
        }

        @Test
        void 비밀번호가_다르면_false를_반환한다() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.confirmPassword("password1")).isFalse();
        }
    }

    @Nested
    class isBuyer_메소드는 {

        @Test
        void 구매자라면_true를_반환한다() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.isBuyer()).isTrue();
        }

        @Test
        void 판매자라면_false를_반환한다() {
            // given
            Member seller = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.SELLER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(seller.isBuyer()).isFalse();
        }
    }

    @Test
    void 동일한_사용자인지_확인할_수_있다() {
        // given
        Member member = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // when
        boolean isSameMember = member.isSameMember("testId");

        // then
        assertThat(isSameMember).isTrue();
    }
}

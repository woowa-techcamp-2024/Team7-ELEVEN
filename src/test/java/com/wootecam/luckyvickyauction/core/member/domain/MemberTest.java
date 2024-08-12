package com.wootecam.luckyvickyauction.core.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void 회원을_생성할_수_있다() {
        // when
        Member member = new Member("testId", "password", Role.BUYER, new Point(100));

        // then
        assertAll(
                () -> assertThat(member.getSignInId()).isEqualTo("testId"),
                () -> assertThat(member.getPassword()).isEqualTo("password"),
                () -> assertThat(member.getRole()).isEqualTo(Role.BUYER),
                () -> assertThat(member.getPoint()).isEqualTo(new Point(100))
        );
    }

    @Test
    void 포인트를_사용할_수_있다() {
        // given
        Member buyer = new Member("testId", "password", Role.BUYER, new Point(100));
        long price = 10L;
        long quantity = 10L;

        // when
        buyer.usePoint(price * quantity);

        // then
        assertThat(buyer.getPoint()).isEqualTo(new Point(0));
    }

    @Test
    void 보유한_포인트보다_많은_포인트를_사용하려하면_예외가_발생한다() {
        // given
        Member buyer = new Member("testId", "password", Role.BUYER, new Point(100));

        // expect
        assertThatThrownBy(() -> buyer.usePoint(10 * 11))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("포인트가 부족합니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.P001));
    }

    @Test
    void 포인트를_충전할_수_있다() {
        // given
        Member seller = new Member("testID", "password", Role.SELLER, new Point(0));

        // when
        seller.chargePoint(100);

        // then
        assertThat(seller.getPoint()).isEqualTo(new Point(100));
    }
}

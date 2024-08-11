package com.wootecam.luckyvickyauction.core.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class BuyerTest {

    @Test
    void 특정_상품_가격_곱하기_수량_만큼_포인트를_사용할_수_있다() {
        // given
        Buyer buyer = new Buyer("testId", Role.BUYER, new Point(100));

        // when
        buyer.usePoint(10, 10);

        // then
        assertThat(buyer.getPoint()).isEqualTo(new Point(0));
    }

    @Test
    void 보유한_포인트보다_많은_포인트를_사용하려하면_예외가_발생한다() {
        // given
        Buyer buyer = new Buyer("testId", Role.BUYER, new Point(100));

        // expect
        assertThatThrownBy(() -> buyer.usePoint(10, 11))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("포인트가 부족합니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.P002));
    }
}

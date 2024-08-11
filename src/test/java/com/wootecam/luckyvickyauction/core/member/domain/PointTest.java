package com.wootecam.luckyvickyauction.core.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class PointTest {

    @Test
    void 포인트를_사용할_수_있다() {
        // given
        Point point = new Point(100);

        // when
        point.minus(100);

        // then
        assertThat(point.getAmount()).isEqualTo(0);
    }

    @Test
    void 포인트_잔액보다_많은_양을_사용하려하면_예외가_발생한다() {
        // given
        Point point = new Point(100);

        // expect
        assertThatThrownBy(() -> point.minus(101))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("포인트가 부족합니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.P002));
    }
}

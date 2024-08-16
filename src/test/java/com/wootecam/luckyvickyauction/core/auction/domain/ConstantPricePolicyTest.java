package com.wootecam.luckyvickyauction.core.auction.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class ConstantPricePolicyTest {

    @Test
    void 경매_변동폭은_0원보다_작거나_같다면_예외가_발생합니다() {
        // expect
        assertThatThrownBy(() -> new ConstantPricePolicy(0))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A004);
    }
}

package com.wootecam.luckyvickyauction.core.auction.domain;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void calculatePriceAtVariation_할인_폭_보다_가격이_더_작거나_같다면_예외가_발생한다() {
        // given
        ConstantPricePolicy constantPricePolicy = new ConstantPricePolicy(100L);

        // expect
        assertThatThrownBy(() -> constantPricePolicy.calculatePriceAtVariation(100L, 10))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(String.format("상품 원가는 가격 변동폭보다 커야 합니다. 상품 원가: %d, 가격 변동폭: %d", 100L, 100L))
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A008);
    }

    @Test
    void calculatePriceAtVariation_가격과_할인횟수가_주어지면_횟수만큼_할인이_적용된_가격을_계산한다() {
        // given
        ConstantPricePolicy constantPricePolicy = new ConstantPricePolicy(100L);

        // when
        long result = constantPricePolicy.calculatePriceAtVariation(10000L, 10L);

        // then
        assertThat(result).isEqualTo(9000L);
    }
}

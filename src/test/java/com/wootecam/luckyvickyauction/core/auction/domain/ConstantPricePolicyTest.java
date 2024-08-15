package com.wootecam.luckyvickyauction.core.auction.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConstantPricePolicyTest {

    @Test
    void 경매_변동폭은_0원보다_작거나_같다면_예외가_발생합니다() {
        // expect
        assertThatThrownBy(() -> new ConstantPricePolicy(0))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A004);
    }

    @Test
    void 경매_진행_중_가격이_0원_이하가_되지_않는_경우_예외가_발생하지_않아야_한다() {
        // given
        ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
        ZonedDateTime startedAt = ZonedDateTime.now();
        ZonedDateTime finishedAt = startedAt.plusMinutes(30);
        Duration variationDuration = Duration.ofMinutes(10);
        long initialPrice = 1000;

        // expect
        assertThatNoException().isThrownBy(
                () -> pricePolicy.validate(startedAt, finishedAt, variationDuration, initialPrice));
    }

    @Test
    void 경매_진행_중_가격이_0원_이하가_되는_경우_예외가_발생한다() {
        // given
        ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
        ZonedDateTime startedAt = ZonedDateTime.now();
        ZonedDateTime finishedAt = startedAt.plusMinutes(60);
        Duration variationDuration = Duration.ofMinutes(10);
        long initialPrice = 500;

        // expect
        assertThatThrownBy(() -> pricePolicy.validate(startedAt, finishedAt, variationDuration, initialPrice))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: 500, 총 가격 하락: 500")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A028);
    }

    @ParameterizedTest
    @CsvSource({
            "1000, 60, 10, true",
            "500, 60, 10, false",
            "1000, 30, 10, true",
            "300, 30, 5, false",
    })
    void 다양한_시나리오에서_고정_변동_정책의_가격_검증이_올바르게_동작해야_한다(
            long initialPrice, long durationMinutes, long variationMinutes, boolean shouldPass) {
        // given
        ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
        ZonedDateTime startedAt = ZonedDateTime.now();
        ZonedDateTime finishedAt = startedAt.plusMinutes(durationMinutes);
        Duration variationDuration = Duration.ofMinutes(variationMinutes);

        // expect
        if (shouldPass) {
            assertThatNoException().isThrownBy(
                    () -> pricePolicy.validate(startedAt, finishedAt, variationDuration, initialPrice));
            return;
        }
        String message = String.format(
                "경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 총 가격 하락: %d",
                initialPrice,
                (durationMinutes / variationMinutes - 1) * 100
        );
        assertThatThrownBy(() -> pricePolicy.validate(startedAt, finishedAt, variationDuration, initialPrice))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(message)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A028);
    }
}

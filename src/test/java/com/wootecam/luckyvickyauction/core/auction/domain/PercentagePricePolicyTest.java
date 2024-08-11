package com.wootecam.luckyvickyauction.core.auction.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PercentagePricePolicyTest {

    @ParameterizedTest
    @ValueSource(doubles = {0, 100})
    @DisplayName("경매 할인율은 0보다 크고 100보다 작지 않다면 BadRequestException이 발생한다.")
    void validate(double discountRate) {
        assertThatThrownBy(() -> new PercentagePricePolicy(discountRate))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A010);
    }

}



package com.wootecam.luckyvickyauction.core.payment.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReceiptSelectConditionTest {

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/158">[FEATURE] 구매자는 거래 목록을 최대 100개까지 한번에
     * 조회할 수 있다.</a>
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 101})
    public void size가_1미만이거나_100초과인_경우_예외가_발생한다(int size) {

        assertThatThrownBy(() -> new ReceiptSelectCondition(size))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.G001);
    }
}
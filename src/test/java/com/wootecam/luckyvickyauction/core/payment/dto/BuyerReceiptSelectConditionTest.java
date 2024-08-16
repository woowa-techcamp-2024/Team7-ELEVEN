package com.wootecam.luckyvickyauction.core.payment.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/158">[FEATURE] 구매자는 거래 목록을 최대 100개까지 한번에
 * 조회할 수 있다.</a>
 */
class BuyerReceiptSelectConditionTest {

    @Test
    void 정상적인_조건이면_생성아_완료된다() {
        // given
        Long buyerId = 1L;
        int size = 10;

        // when
        BuyerReceiptSearchCondition buyerReceiptSearchCondition = new BuyerReceiptSearchCondition(buyerId, size);

        // then
        assertAll(
                () -> assertThat(buyerReceiptSearchCondition.buyerId()).isEqualTo(buyerId),
                () -> assertThat(buyerReceiptSearchCondition.size()).isEqualTo(size)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 101})
    public void size가_1미만이거나_100초과인_경우_예외가_발생한다(int size) {

        assertThatThrownBy(() -> new BuyerReceiptSearchCondition(1L, size))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.G001);
    }
}
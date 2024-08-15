package com.wootecam.luckyvickyauction.core.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class BidHistoryTest {

    @Test
    void 성공적으로_환불_표시를_하고_업데이트_시간을_갱신_한다() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        BidHistory refundBidHistory = BidHistory.builder()
                .id(1L)
                .auctionId(1L)
                .productName("test")
                .price(100L)
                .quantity(1L)
                .bidStatus(BidStatus.BID)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // when
        refundBidHistory.markAsRefund(now.plusMinutes(1));

        // then
        assertThat(refundBidHistory.getBidStatus()).isEqualTo(BidStatus.REFUND);
        assertThat(refundBidHistory.getUpdatedAt()).isEqualTo(now.plusMinutes(1));
    }

    @Test
    void 이미_환불된_경매에_환불_표시를_하면_예외가_발생한다() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        BidHistory refundBidHistory = BidHistory.builder()
                .bidStatus(BidStatus.REFUND)
                .build();

        // expect
        assertThatThrownBy(() -> refundBidHistory.markAsRefund(now))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 환불된 입찰 내역입니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.B005));

    }
}

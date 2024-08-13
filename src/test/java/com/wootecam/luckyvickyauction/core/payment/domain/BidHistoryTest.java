package com.wootecam.luckyvickyauction.core.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class BidHistoryTest {

    @Test
    void 거래_내역이_환불_상태인지_확인할_수_있다() {
        // given
        BidHistory refundBidHistory = BidHistory.builder()
                .bidStatus(BidStatus.REFUND)
                .build();

        // when
        boolean isRefundStatus = refundBidHistory.isRefundStatus();

        // then
        assertThat(isRefundStatus).isTrue();
    }
}

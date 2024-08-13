package com.wootecam.luckyvickyauction.core.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.repository.FakeBidHistoryRepository;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BidHistoryServiceTest {
    private BidHistoryService bidHistoryService;
    private BidHistoryRepository bidHistoryRepository;

    @BeforeEach
    void setUp() {
        bidHistoryRepository = new FakeBidHistoryRepository();
        bidHistoryService = new BidHistoryService(bidHistoryRepository);
    }

    @Nested
    class getBidHistory_메서드는 {

        @Test
        @Disabled
        void 성공하는_경우_BidHistoryInfo를_반환받는다() {
            throw new UnsupportedOperationException();
        }

        @Test
        void 존재하지않는_거래내역을_조회할때_예외가_발생한다() {
            // given
            long bidHistoryId = 1L;

            // expect
            assertThatThrownBy(() -> bidHistoryService.getBidHistoryInfo(bidHistoryId))
                    .isInstanceOf(NotFoundException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.P002));
        }

        @Test
        @Disabled
        void 해당_거래내역의_소유자가_아닌경우_예외가_발생한다() {
            throw new UnsupportedOperationException();
        }

        @Test
        @Disabled
        void 잘못된_인자를_전달받는_경우_예외가_발생한다() {
            throw new UnsupportedOperationException();
        }
    }

}

package com.wootecam.luckyvickyauction.core.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.repository.FakeBidHistoryRepository;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
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
            Member member = Member.builder().build();
            long bidHistoryId = 1L;

            // expect
            assertThatThrownBy(() -> bidHistoryService.getBidHistoryInfo(member, bidHistoryId))
                    .isInstanceOf(NotFoundException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.B000));
        }

        @Test
        void 해당_거래내역의_소유자가_아닌경우_예외가_발생한다() {

            // given
            Member seller = Member.builder().id(1L).signInId("판매자").build();  // 소유자
            Member buyer = Member.builder().id(2L).signInId("구매자").build();  // 소유자
            Member nonOwner = Member.builder().id(3L).signInId("나쁜놈").build();  // 비소유자

            BidHistory bidHistory = BidHistory.builder()
                    .id(1L)
                    .seller(seller)
                    .buyer(buyer)
                    .build();
            bidHistoryRepository.save(bidHistory);

            // expect
            assertThatThrownBy(() -> bidHistoryService.getBidHistoryInfo(nonOwner, 1L))
                    .isInstanceOf(UnauthorizedException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.B001));
        }

    }

}

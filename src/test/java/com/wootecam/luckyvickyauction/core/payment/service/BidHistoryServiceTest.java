package com.wootecam.luckyvickyauction.core.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import com.wootecam.luckyvickyauction.core.payment.infra.FakeBidHistoryRepository;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

        static Stream<Arguments> provideMembersForSuccess() {
            Member seller = MemberFixture.createSellerWithDefaultPoint();  // 소유자
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();  // 소유자

            return Stream.of(
                    Arguments.of(seller.getId(), "판매자의 구매이력 조회"),
                    Arguments.of(buyer.getId(), "구매자의 구매이력 조회")
            );
        }

        @ParameterizedTest(name = "{1} 시 성공한다")
        @MethodSource("provideMembersForSuccess")
        void 소유자가_거래내역_조회시_성공한다(Long memberId, String description) {
            // given
            ZonedDateTime now = ZonedDateTime.now();
            Member seller = MemberFixture.createSellerWithDefaultPoint();  // 소유자
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();  // 소유자

            BidHistory bidHistory = BidHistory.builder()
                    .id(1L)
                    .productName("멋진 상품")
                    .price(1000000)
                    .quantity(1)
                    .bidStatus(BidStatus.BID)
                    .auctionId(1L)
                    .sellerId(seller.getId())
                    .buyerId(buyer.getId())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            bidHistoryRepository.save(bidHistory);

            // when
            BidHistoryInfo bidHistoryInfo = bidHistoryService.getBidHistoryInfo(1L);

            // then
            assertAll(
                    () -> assertThat(bidHistoryInfo.auctionId()).isEqualTo(1L),
                    () -> assertThat(bidHistoryInfo.productName()).isEqualTo("멋진 상품"),
                    () -> assertThat(bidHistoryInfo.price()).isEqualTo(1000000),
                    () -> assertThat(bidHistoryInfo.quantity()).isEqualTo(1),
                    () -> assertThat(bidHistoryInfo.bidStatus()).isEqualTo(BidStatus.BID),
                    () -> assertThat(bidHistoryInfo.auctionId()).isEqualTo(1L),
                    () -> assertThat(bidHistoryInfo.sellerId()).isEqualTo(seller.getId()),
                    () -> assertThat(bidHistoryInfo.buyerId()).isEqualTo(buyer.getId())
            );
        }

        @Test
        void 존재하지않는_거래내역을_조회할때_예외가_발생한다() {
            // given
            Member member = MemberFixture.createBuyerWithDefaultPoint();
            long bidHistoryId = 1L;

            // expect
            assertThatThrownBy(() -> bidHistoryService.getBidHistoryInfo(bidHistoryId))
                    .isInstanceOf(NotFoundException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.B000));
        }

        // TODO [추후 소유자 인증 객체 생성 후 구현] [writeAt: 2024/08/18/16:21] [writeBy: minseok-oh]

//        @Test
//        void 해당_거래내역의_소유자가_아닌경우_예외가_발생한다() {
//
//            // given
//            Member seller = Member.builder().id(1L).signInId("판매자").password("password00").role(Role.SELLER)
//                    .build();  // 소유자
//            Member buyer = Member.builder().id(2L).signInId("구매자").password("password00").role(Role.BUYER)
//                    .build();  // 소유자
//            Member nonOwner = Member.builder().id(3L).signInId("나쁜놈").password("password00").role(Role.BUYER)
//                    .build();  // 비소유자
//
//            BidHistory bidHistory = BidHistory.builder()
//                    .id(1L)
//                    .sellerId(seller.getId())
//                    .buyerId(buyer.getId())
//                    .build();
//            bidHistoryRepository.save(bidHistory);
//
//            // expect
//            assertThatThrownBy(() -> bidHistoryService.getBidHistoryInfo(1L))
//                    .isInstanceOf(UnauthorizedException.class)
//                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
//                            ErrorCode.B001));
//        }

//        @Test
//        void 다른_판매자의_구매이력_조회시_예외가_발생한다() {
//            // given
//            ZonedDateTime now = ZonedDateTime.now();
//            Member seller1 = Member.builder().id(1L).signInId("판매자").password("password00").role(Role.SELLER)
//                    .build();  // 판매자
//            Member seller2 = Member.builder().id(2L).signInId("옆집 사장님").password("password00").role(Role.SELLER)
//                    .build();  // 판매자
//            Member buyer = Member.builder().id(3L).signInId("구매자").password("password00").role(Role.BUYER)
//                    .build();  // 구매자
//
//            BidHistory bidHistory = BidHistory.builder()
//                    .id(1L)
//                    .productName("멋진 상품")
//                    .price(1000000)
//                    .quantity(1)
//                    .bidStatus(BidStatus.BID)
//                    .auctionId(1L)
//                    .sellerId(seller1.getId())
//                    .buyerId(buyer.getId())
//                    .createdAt(now)
//                    .updatedAt(now)
//                    .build();
//            bidHistoryRepository.save(bidHistory);
//
//            // expect
//            assertThatThrownBy(() -> bidHistoryService.getBidHistoryInfo(1L))
//                    .isInstanceOf(UnauthorizedException.class)
//                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
//                            ErrorCode.B001));
//        }

    }

}

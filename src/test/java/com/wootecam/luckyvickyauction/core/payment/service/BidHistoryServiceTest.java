package com.wootecam.luckyvickyauction.core.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.context.ServiceTest;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BidHistoryServiceTest extends ServiceTest {

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
            Member buyer = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
            Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());

            BidHistory bidHistory = BidHistory.builder()
                    .id(1L)
                    .productName("멋진 상품")
                    .price(1000000)
                    .quantity(1)
                    .bidStatus(BidStatus.PURCHASED)
                    .auctionId(1L)
                    .sellerId(seller.getId())
                    .buyerId(buyer.getId())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            bidHistoryRepository.save(bidHistory);

            // when
            BidHistoryInfo bidHistoryInfo = bidHistoryService.getBidHistoryInfo(
                    new SignInInfo(seller.getId(), Role.SELLER), 1L);

            // then
            assertAll(
                    () -> assertThat(bidHistoryInfo.auctionId()).isEqualTo(1L),
                    () -> assertThat(bidHistoryInfo.productName()).isEqualTo("멋진 상품"),
                    () -> assertThat(bidHistoryInfo.price()).isEqualTo(1000000),
                    () -> assertThat(bidHistoryInfo.quantity()).isEqualTo(1),
                    () -> assertThat(bidHistoryInfo.bidStatus()).isEqualTo(BidStatus.PURCHASED),
                    () -> assertThat(bidHistoryInfo.auctionId()).isEqualTo(1L),
                    () -> assertThat(bidHistoryInfo.sellerId()).isEqualTo(seller.getId()),
                    () -> assertThat(bidHistoryInfo.buyerId()).isEqualTo(buyer.getId())
            );
        }

        @Test
        void 존재하지않는_거래내역을_조회할때_예외가_발생한다() {
            // given
            Member member = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
            long bidHistoryId = 1L;

            // expect
            assertThatThrownBy(
                    () -> bidHistoryService.getBidHistoryInfo(new SignInInfo(member.getId(), Role.BUYER), bidHistoryId))
                    .isInstanceOf(NotFoundException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.B000));
        }

        @Test
        void 해당_거래내역의_소유자가_아닌경우_예외가_발생한다() {
            // given
            Member seller = Member.builder().id(1L).signInId("판매자").password("password00").role(Role.SELLER)
                    .build();  // 소유자
            Member buyer = Member.builder().id(2L).signInId("구매자").password("password00").role(Role.BUYER)
                    .build();  // 소유자
            SignInInfo nonOwner = new SignInInfo(3L, Role.BUYER);

            BidHistory bidHistory = BidHistory.builder()
                    .id(1L)
                    .sellerId(seller.getId())
                    .buyerId(buyer.getId())
                    .build();
            bidHistoryRepository.save(bidHistory);

            // expect
            assertThatThrownBy(() -> bidHistoryService.getBidHistoryInfo(nonOwner, 1L))
                    .isInstanceOf(UnauthorizedException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.B001));
        }
    }

    @Nested
    class getBuyerReceiptSimpleInfos_메소드는 {

        @Nested
        class 정상적인_요청이_들어오면 {

            @Test
            void 특정_구매자의_거래이력을_조회할_수_있다() {
                // given
                Member seller = Member.builder().id(1L).signInId("판매자").password("password00").role(Role.SELLER)
                        .build();
                Member buyer = Member.builder().id(2L).signInId("구매자").password("password00").role(Role.BUYER)
                        .build();

                BidHistory bidHistory = BidHistory.builder()
                        .id(1L)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .build();
                bidHistoryRepository.save(bidHistory);

                // when
                List<BuyerReceiptSimpleInfo> buyerReceiptSimpleInfos = bidHistoryService.getBuyerReceiptSimpleInfos(
                        new SignInInfo(buyer.getId(), Role.BUYER),
                        new BuyerReceiptSearchCondition(5)
                );

                // then
                assertAll(
                        () -> assertThat(buyerReceiptSimpleInfos).hasSize(1)
                );
            }
        }
    }

    @Nested
    class getSellerReceiptSimpleInfos_메소드는 {

        @Nested
        class 정상적인_요청이_들어오면 {

            @Test
            void 특정_구매자의_거래이력을_조회할_수_있다() {
                // given
                Member seller = Member.builder().id(1L).signInId("판매자").password("password00").role(Role.SELLER)
                        .build();
                Member buyer = Member.builder().id(2L).signInId("구매자").password("password00").role(Role.BUYER)
                        .build();

                BidHistory bidHistory = BidHistory.builder()
                        .id(1L)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .build();
                bidHistoryRepository.save(bidHistory);

                // when
                List<SellerReceiptSimpleInfo> sellerReceiptSimpleInfos = bidHistoryService.getSellerReceiptSimpleInfos(
                        new SignInInfo(seller.getId(), Role.SELLER),
                        new SellerReceiptSearchCondition(5)
                );

                // then
                assertAll(
                        () -> assertThat(sellerReceiptSimpleInfos).hasSize(1)
                );
            }
        }
    }
}

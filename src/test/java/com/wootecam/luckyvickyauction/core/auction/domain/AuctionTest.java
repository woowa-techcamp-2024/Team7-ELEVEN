package com.wootecam.luckyvickyauction.core.auction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuctionTest {

    @Nested
    @DisplayName("절대값 가격 정책 적용시")
    public class ConstantsPricePolicyTest {

        @Test
        @DisplayName("경매 생성시 경매의 가격은 가격 변동폭보다 작거나 같으면 예외가 발생한다.")
        public void createAuctionPriceShouldBeGreaterThanVariationWidth() {
            // given
            int originPrice = 10000;
            int stock = 999999;
            int maximumPurchaseLimitCount = 10;

            int variationWidth = 10000;
            Duration varitationDuration = Duration.ofMinutes(1L);
            PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

            // when & then
            assertThatThrownBy(() ->
                    Auction.builder()
                            .sellerId(1L)
                            .productName("상품이름")
                            .originPrice(originPrice)
                            .currentPrice(originPrice)
                            .originStock(stock)
                            .currentStock(stock)
                            .pricePolicy(pricePolicy)
                            .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                            .variationDuration(varitationDuration)
                            .startedAt(ZonedDateTime.now().minusHours(1L))
                            .finishedAt(ZonedDateTime.now())
                            .isShowStock(true)
                            .status(AuctionStatus.WAITING)
                            .build()
            ).isInstanceOf(BadRequestException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A009);
        }
    }

    @Nested
    @DisplayName("퍼센트 가격 정책 적용시")
    public class PercentagePricePolicyTest {
    }

    @Nested
    @DisplayName("경매 정책 변경 기능")
    public class UpdateAuctionTest {

        @Test
        @DisplayName("동일한 판매자는 현재 경매의 가격 노출 정책을 변경할 수 있다.")
        public void updateAuctionShowStock() {
            // given
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(999999)
                    .currentStock(999999)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(1L))
                    .startedAt(ZonedDateTime.now().minusHours(1L))
                    .finishedAt(ZonedDateTime.now())
                    .isShowStock(true)
                    .status(AuctionStatus.WAITING)
                    .build();

            Long requestSellerId = 1L;

            // when
            auction.updateShowStock(false, requestSellerId);

            // then
            assertThat(auction.isShowStock()).isFalse();
        }

        @Test
        @DisplayName("동일한 판매자가 아닌 경우 가격 노출 정책을 변경할 수 없다.")
        public void dd() {
            // given
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(999999)
                    .currentStock(999999)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(1L))
                    .startedAt(ZonedDateTime.now().minusHours(1L))
                    .finishedAt(ZonedDateTime.now())
                    .isShowStock(true)
                    .status(AuctionStatus.WAITING)
                    .build();

            Long requestSellerId = 2L;

            // when & then
            assertThatThrownBy(() -> auction.updateShowStock(false, requestSellerId))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A015);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("가격 노출 정책이 null인 경우 변경이 반영되지 않는다.")
        public void updateShowStockWhenShowStockIsNull(boolean isShowStock) {
            // given
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(999999)
                    .currentStock(999999)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(1L))
                    .startedAt(ZonedDateTime.now().minusHours(1L))
                    .finishedAt(ZonedDateTime.now())
                    .isShowStock(isShowStock)
                    .build();

            Long requestSellerId = 1L;

            // when
            auction.updateShowStock(null, requestSellerId);

            // then
            assertThat(auction.isShowStock()).isEqualTo(isShowStock);
        }
    }

    @Test
    @DisplayName("판매자는 경매의 가격 정책을 변경할 수 있다.")
    public void updateAuctionPolicy() {
        // given
        long sellerId = 1L;
        Auction auction = Auction.builder()
                .sellerId(sellerId)
                .productName("상품이름")
                .originPrice(10000)
                .currentPrice(10000)
                .originStock(999999)
                .currentStock(999999)
                .pricePolicy(new ConstantPricePolicy(1000))
                .maximumPurchaseLimitCount(10)
                .variationDuration(Duration.ofMinutes(1L))
                .startedAt(ZonedDateTime.now().minusHours(1L))
                .finishedAt(ZonedDateTime.now())
                .isShowStock(true)
                .build();

        PricePolicy newPricePolicy = PricePolicy.createPercentagePricePolicy(7);

        // when
        auction.updatePricePolicy(newPricePolicy, sellerId);

        // then
        assertThat(auction.getPricePolicy()).isEqualTo(newPricePolicy);
    }

    @Test
    @DisplayName("가격 정책이 null인 경우 변경이 반영되지 않는다.")
    public void updatePricePolicyWhenPricePolicyIsNull() {
        // given
        long sellerId = 1L;
        Auction auction = Auction.builder()
                .sellerId(sellerId)
                .productName("상품이름")
                .originPrice(10000)
                .originStock(999999)
                .currentStock(999999)
                .pricePolicy(new ConstantPricePolicy(1000))
                .maximumPurchaseLimitCount(10)
                .variationDuration(Duration.ofMinutes(1L))
                .startedAt(ZonedDateTime.now().minusHours(1L))
                .finishedAt(ZonedDateTime.now())
                .isShowStock(true)
                .status(AuctionStatus.WAITING)
                .build();

        // when
        auction.updatePricePolicy(null, sellerId);

        // then
        assertThat(auction.getPricePolicy()).isNotNull();
    }

    @Nested
    class changeStock_메소드는 {

        @Test
        void 변경할_재고가_1개_미만이면_예외가_발생한다() {
            // given
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(999999)
                    .currentStock(999999)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(1L))
                    .startedAt(ZonedDateTime.now().minusHours(1L))
                    .finishedAt(ZonedDateTime.now())
                    .isShowStock(true)
                    .status(AuctionStatus.WAITING)
                    .build();

            // expect
            assertThatThrownBy(() -> auction.changeStock(0))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("변경 할 재고는 1개 이상이어야 합니다. inputStock=0")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A019));
        }

        @Test
        void 변경할_재고가_1개_이상이라면_정상적으로_재고가_변경된다() {
            // given
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(999999)
                    .currentStock(999999)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(1L))
                    .startedAt(ZonedDateTime.now().minusHours(1L))
                    .finishedAt(ZonedDateTime.now())
                    .isShowStock(true)
                    .status(AuctionStatus.WAITING)
                    .build();

            // when
            auction.changeStock(1L);

            // then
            assertThat(auction.getCurrentStock()).isEqualTo(1L);
        }
    }

    @Nested
    class updateCurrentStock_메소드는 {

        @Nested
        class 정상적인_요청이_오면 {

            @Test
            void 재고_변경이_완료된다() {
                // given
                long originStock = 999999L;
                long currentStock = 1L;
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(10000)
                        .currentPrice(10000)
                        .originStock(originStock)
                        .currentStock(currentStock)
                        .pricePolicy(new ConstantPricePolicy(1000))
                        .maximumPurchaseLimitCount(10)
                        .variationDuration(Duration.ofMinutes(1L))
                        .startedAt(ZonedDateTime.now().minusHours(1L))
                        .finishedAt(ZonedDateTime.now())
                        .isShowStock(true)
                        .status(AuctionStatus.WAITING)
                        .build();

                // when
                auction.updateCurrentStock(1L);

                // then
                assertThat(auction.getCurrentStock()).isEqualTo(2L);
            }
        }

        @Nested
        class 만약_변경_후_재고가_음수라면 {

            @Test
            void 예외가_발생한다() {
                // given
                long originStock = 999999L;
                long currentStock = 0L;
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(10000)
                        .currentPrice(10000)
                        .originStock(originStock)
                        .currentStock(currentStock)
                        .pricePolicy(new ConstantPricePolicy(1000))
                        .maximumPurchaseLimitCount(10)
                        .variationDuration(Duration.ofMinutes(1L))
                        .startedAt(ZonedDateTime.now().minusHours(1L))
                        .finishedAt(ZonedDateTime.now())
                        .isShowStock(true)
                        .status(AuctionStatus.WAITING)
                        .build();

                // expect
                assertThatThrownBy(() -> auction.updateCurrentStock(-1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("변경할 재고는 0보다 작을 수 없습니다. inputStock=-1")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.A022));
            }
        }

        @Nested
        class 만약_변경_후_재고가_원래_재고보다_많다면 {

            @Test
            void 예외가_발생한다() {
                // given
                long originStock = 999999L;
                long currentStock = 999999L;
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(10000)
                        .currentPrice(10000)
                        .originStock(originStock)
                        .currentStock(currentStock)
                        .pricePolicy(new ConstantPricePolicy(1000))
                        .maximumPurchaseLimitCount(10)
                        .variationDuration(Duration.ofMinutes(1L))
                        .startedAt(ZonedDateTime.now().minusHours(1L))
                        .finishedAt(ZonedDateTime.now())
                        .isShowStock(true)
                        .status(AuctionStatus.WAITING)
                        .build();

                // expect
                assertThatThrownBy(() -> auction.updateCurrentStock(1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("변경할 재고는 원래 재고보다 많을 수 없습니다. inputStock=1")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.A023));
                auction.updateCurrentStock(-1L);
            }
        }
    }
}

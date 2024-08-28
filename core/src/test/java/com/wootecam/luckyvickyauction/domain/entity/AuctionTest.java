package com.wootecam.luckyvickyauction.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

import com.wootecam.luckyvickyauction.domain.entity.type.AuctionStatus;
import com.wootecam.luckyvickyauction.domain.entity.type.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.domain.entity.type.PercentagePricePolicy;
import com.wootecam.luckyvickyauction.domain.entity.type.PricePolicy;
import com.wootecam.luckyvickyauction.exception.BadRequestException;
import com.wootecam.luckyvickyauction.exception.ErrorCode;
import com.wootecam.luckyvickyauction.fixture.AuctionFixture;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class AuctionTest {

    @Nested
    class Auction을_생성할_때 {

        @Nested
        class 고정_가격_변동_정책을_이용하는_경우 {

            @Test
            void 하락하는_가격의_최소값이_0원_이하가_되지_않는_경우_경매가_정상_생성된다() {
                // given
                ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(30);
                Duration variationDuration = Duration.ofMinutes(10);
                long initialPrice = 1000L;

                // expect
                assertThatNoException().isThrownBy(
                        () -> Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build());
            }

            @Test
            public void 경매의_가격은_가격_변동폭보다_작거나_같으면_예외가_발생한다() {
                // given
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 10000;
                Duration varitationDuration = Duration.ofMinutes(1L);
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);
                LocalDateTime now = LocalDateTime.now();

                // expect
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
                                .startedAt(now.minusHours(1L))
                                .finishedAt(now)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A008);
            }

            @Test
            void 경매_진행_중_가격이_0원_이하가_되는_경우_예외가_발생한다() {
                // given
                ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(60);
                Duration variationDuration = Duration.ofMinutes(10);
                long initialPrice = 500;

                // expect
                assertThatThrownBy(() ->
                        Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: 500, 할인횟수: 5, 모든 할인 적용 후 가격: 0")
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A021);
            }

            @ParameterizedTest
            @CsvSource({
                    "1000, 60, 10, true",
                    "500, 60, 10, false",
                    "1000, 30, 10, true",
                    "300, 30, 5, false",
            })
            void 다양한_시나리오에서_가격_검증이_올바르게_동작해야_한다(
                    long initialPrice, long durationMinutes, long variationMinutes, boolean shouldPass) {
                // given
                ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(durationMinutes);
                Duration variationDuration = Duration.ofMinutes(variationMinutes);

                // expect
                if (shouldPass) {
                    assertThatNoException().isThrownBy(
                            () -> Auction.builder()
                                    .sellerId(1L)
                                    .productName("productName")
                                    .originPrice(initialPrice)
                                    .currentPrice(initialPrice)
                                    .originStock(100L)
                                    .currentStock(100L)
                                    .maximumPurchaseLimitCount(10L)
                                    .pricePolicy(pricePolicy)
                                    .variationDuration(variationDuration)
                                    .startedAt(startedAt)
                                    .finishedAt(finishedAt)
                                    .isShowStock(true)
                                    .build());
                    return;
                }
                assertThatThrownBy(() ->
                        Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format(
                                "경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 할인횟수: %d, 모든 할인 적용 후 가격: %d",
                                initialPrice,
                                durationMinutes / variationMinutes - 1,
                                initialPrice - (durationMinutes / variationMinutes - 1) * 100))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A021);
            }
        }

        @Nested
        class 퍼센트_가격_변동_정책을_이용하는_경우 {

            @Test
            void 하락하는_가격의_최소값이_0원_이하가_되지_않는_경우_경매가_정상_생성된다() {
                // given
                PercentagePricePolicy pricePolicy = new PercentagePricePolicy(50.0);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(10);
                Duration variationDuration = Duration.ofMinutes(1);
                long initialPrice = 512;

                // expect
                assertThatNoException().isThrownBy(
                        () -> Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build());
            }

            @Test
            void validate메소드는_경매_진행_중_가격이_0원_이하가_되는_경우_예외가_발생한다() {
                // given
                PercentagePricePolicy pricePolicy = new PercentagePricePolicy(50.0);
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(10);
                Duration variationDuration = Duration.ofMinutes(1);
                long initialPrice = 256;

                // expect
                assertThatThrownBy(() ->
                        Auction.builder()
                                .sellerId(1L)
                                .productName("productName")
                                .originPrice(initialPrice)
                                .currentPrice(initialPrice)
                                .originStock(100L)
                                .currentStock(100L)
                                .maximumPurchaseLimitCount(10L)
                                .pricePolicy(pricePolicy)
                                .variationDuration(variationDuration)
                                .startedAt(startedAt)
                                .finishedAt(finishedAt)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format(
                                "경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 할인횟수: %d, 모든 할인 적용 후 가격: %d",
                                initialPrice,
                                9,
                                0))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A021);
            }
        }

        @Nested
        class 경매_시간에서_할인_주기_시간이_나누어_떨어지지_않는다면 {

            @ParameterizedTest
            @ValueSource(ints = {11, 7, 13, 21, 31, 14})
            void 예외가_발생한다(int invalidVariationDuration) {
                // given
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 10000;
                Duration varitationDuration = Duration.ofSeconds(invalidVariationDuration);
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);
                LocalDateTime now = LocalDateTime.now();

                // expect
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
                                .startedAt(now)
                                .finishedAt(now.plusMinutes(60L))
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A028);
            }
        }

        @Nested
        class 경매_주기_시간이_60분을_넘긴다면 {

            @Test
            void 예외가_발생합니다() {
                // given
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(60).plusNanos(1L);
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

                // expect
                assertThatThrownBy(() -> Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(originPrice)
                        .currentPrice(originPrice)
                        .originStock(stock)
                        .currentStock(stock)
                        .pricePolicy(pricePolicy)
                        .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                        .variationDuration(Duration.ofMinutes(10))
                        .startedAt(startedAt)
                        .finishedAt(finishedAt)
                        .isShowStock(true)
                        .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A007);
            }
        }

        @Nested
        class 경매_주기_시간이_분_단위가_아니라면 {

            @Test
            void 예외가_발생합니다() {
                // given
                LocalDateTime startedAt = LocalDateTime.now();
                LocalDateTime finishedAt = startedAt.plusMinutes(1).plusNanos(1L);
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

                // expect
                assertThatThrownBy(() -> Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(originPrice)
                        .currentPrice(originPrice)
                        .originStock(stock)
                        .currentStock(stock)
                        .pricePolicy(pricePolicy)
                        .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                        .variationDuration(Duration.ofSeconds(1L))
                        .startedAt(startedAt)
                        .finishedAt(finishedAt)
                        .isShowStock(true)
                        .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A029);
            }
        }

        @Nested
        class 경매_시간에서_할인_주기_시간이_나누어_떨어진다면 {

            @ParameterizedTest
            @ValueSource(ints = {10, 6, 12, 30, 20, 15})
            void 경매를_정상_생성한다(int invalidVariationDuration) {
                // given
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                Duration varitationDuration = Duration.ofSeconds(invalidVariationDuration);
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);
                LocalDateTime now = LocalDateTime.now();

                // expect
                assertThatNoException().isThrownBy(() -> Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(originPrice)
                        .currentPrice(originPrice)
                        .originStock(stock)
                        .currentStock(stock)
                        .pricePolicy(pricePolicy)
                        .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                        .variationDuration(varitationDuration)
                        .startedAt(now)
                        .finishedAt(now.plusMinutes(1L))
                        .isShowStock(true)
                        .build());
            }
        }
    }

    @Nested
    class refundStock_메소드는 {

        @Nested
        class 정상적인_요청이_오면 {

            @Test
            void 환불이_완료된다() {
                // given
                long originStock = 999999L;
                long currentStock = 999998L;
                LocalDateTime now = LocalDateTime.now();
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(10000)
                        .currentPrice(10000)
                        .originStock(originStock)
                        .currentStock(currentStock)
                        .pricePolicy(new ConstantPricePolicy(1000))
                        .maximumPurchaseLimitCount(10)
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusMinutes(30))
                        .finishedAt(now.plusMinutes(30))
                        .isShowStock(true)
                        .build();

                // when
                auction.refundStock(1L);

                // then
                assertThat(auction.getCurrentStock()).isEqualTo(999999L);
            }
        }

        @Nested
        class 만약_환불할_재고량이_0_이하라면 {

            @Test
            void 예외가_발생한다() {
                // given
                long originStock = 999999L;
                long currentStock = 0L;
                LocalDateTime now = LocalDateTime.now();
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(10000)
                        .currentPrice(10000)
                        .originStock(originStock)
                        .currentStock(currentStock)
                        .pricePolicy(new ConstantPricePolicy(1000))
                        .maximumPurchaseLimitCount(10)
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusMinutes(30L))
                        .finishedAt(now.plusMinutes(30L))
                        .isShowStock(true)
                        .build();

                // expect
                assertThatThrownBy(() -> auction.refundStock(-1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("환불할 재고는 1보다 작을 수 없습니다. inputStock=-1")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.A015));
            }
        }

        @Nested
        class 만약_환불_후_재고가_원래_재고보다_많다면 {

            @Test
            void 예외가_발생한다() {
                // given
                long originStock = 999999L;
                long currentStock = 999999L;
                LocalDateTime now = LocalDateTime.now();
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(10000)
                        .currentPrice(10000)
                        .originStock(originStock)
                        .currentStock(currentStock)
                        .pricePolicy(new ConstantPricePolicy(1000))
                        .maximumPurchaseLimitCount(10)
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusMinutes(30L))
                        .finishedAt(now.plusMinutes(30L))
                        .isShowStock(true)
                        .build();

                // expect
                assertThatThrownBy(() -> auction.refundStock(1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("환불 후 재고는 원래 재고보다 많을 수 없습니다. inputStock=1")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.A016));
            }
        }
    }

    @Nested
    class submit_메소드는 {

        @Nested
        class 경매상태가_진행중이_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Auction auction = AuctionFixture.createWaitingAuction();

                // expect
                assertThatThrownBy(() -> auction.submit(2000, 10, LocalDateTime.now()))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("진행 중인 경매에만 입찰할 수 있습니다. 현재상태: " + AuctionStatus.WAITING)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A013);
            }
        }

        @Nested
        class 요청시간_기준_현재_가격과_사용자가_요청한_가격이_다르다면 {

            @Test
            void 예외가_발생한다() {
                // given
                LocalDateTime now = LocalDateTime.now();
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("productName")
                        .originPrice(10000L)
                        .currentPrice(10000L)
                        .originStock(100L)
                        .currentStock(100L)
                        .maximumPurchaseLimitCount(10L)
                        .pricePolicy(new ConstantPricePolicy(1000L))
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusMinutes(30))
                        .finishedAt(now.plusMinutes(30))
                        .isShowStock(true)
                        .build();
                LocalDateTime requestTime = now.minusMinutes(30).plusMinutes(33);

                // expect
                assertThatThrownBy(() -> auction.submit(7001L, 10, requestTime))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format("입력한 가격으로 상품을 구매할 수 없습니다. 현재가격: %d 입력가격: %d", 7000L, 7001L))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A022);
            }
        }

        @Nested
        class 구매_불가능한_숫자만큼_요청이_오면 {

            @ParameterizedTest
            @ValueSource(longs = {0L, 31L, 101L})
            void 예외가_발생한다(long requestQuantity) {
                // given
                LocalDateTime now = LocalDateTime.now();
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("productName")
                        .originPrice(10000L)
                        .currentPrice(10000L)
                        .originStock(100L)
                        .currentStock(100L)
                        .maximumPurchaseLimitCount(30L)
                        .pricePolicy(new ConstantPricePolicy(1000L))
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusMinutes(30))
                        .finishedAt(now.plusMinutes(30))
                        .isShowStock(true)
                        .build();

                // expect
                assertThatThrownBy(() -> auction.submit(7000L, requestQuantity, now))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format("해당 수량만큼 구매할 수 없습니다. 재고: %d, 요청: %d, 인당구매제한: %d",
                                auction.getCurrentStock(), requestQuantity, auction.getMaximumPurchaseLimitCount()))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A012);
            }
        }

        @Nested
        class 정상적인_구매요청이_들어오면 {

            @Test
            void 상품의_현재_재고가_차감된다() {
                // given
                LocalDateTime now = LocalDateTime.now();
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("productName")
                        .originPrice(10000L)
                        .currentPrice(10000L)
                        .originStock(100L)
                        .currentStock(100L)
                        .maximumPurchaseLimitCount(30L)
                        .pricePolicy(new ConstantPricePolicy(1000L))
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusMinutes(30))
                        .finishedAt(now.plusMinutes(30))
                        .isShowStock(true)
                        .build();
                LocalDateTime requestTime = now.minusMinutes(30).plusMinutes(33);

                // when
                auction.submit(7000L, 10, requestTime);

                // then
                assertThat(auction.getCurrentStock()).isEqualTo(90L);
            }
        }
    }
}

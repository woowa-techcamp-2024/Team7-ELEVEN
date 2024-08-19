package com.wootecam.luckyvickyauction.core.auction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

import com.wootecam.luckyvickyauction.core.auction.fixture.AuctionFixture;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
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
                ZonedDateTime startedAt = ZonedDateTime.now();
                ZonedDateTime finishedAt = startedAt.plusMinutes(30);
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
                ZonedDateTime now = ZonedDateTime.now();

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
                                .startedAt(now.minusHours(1L))
                                .finishedAt(now)
                                .isShowStock(true)
                                .build())
                        .isInstanceOf(BadRequestException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A009);
            }

            @Test
            void 경매_진행_중_가격이_0원_이하가_되는_경우_예외가_발생한다() {
                // given
                ConstantPricePolicy pricePolicy = new ConstantPricePolicy(100);
                ZonedDateTime startedAt = ZonedDateTime.now();
                ZonedDateTime finishedAt = startedAt.plusMinutes(60);
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
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A028);
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
                ZonedDateTime startedAt = ZonedDateTime.now();
                ZonedDateTime finishedAt = startedAt.plusMinutes(durationMinutes);
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
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A028);
            }
        }

        @Nested
        class 퍼센트_가격_변동_정책을_이용하는_경우 {

            @Test
            void 하락하는_가격의_최소값이_0원_이하가_되지_않는_경우_경매가_정상_생성된다() {
                // given
                PercentagePricePolicy pricePolicy = new PercentagePricePolicy(50.0);
                ZonedDateTime startedAt = ZonedDateTime.now();
                ZonedDateTime finishedAt = startedAt.plusMinutes(10);
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
                ZonedDateTime startedAt = ZonedDateTime.now();
                ZonedDateTime finishedAt = startedAt.plusMinutes(10);
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
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A028);
            }
        }
    }

    @Nested
    public class updateShowStock_메소드는 {

        @Test
        public void 동일한_판매자는_현재_경매의_가격_노출_정책을_변경할_수_있다() {
            // given
            Auction auction = AuctionFixture.createWaitingAuction();

            // when
            auction.updateShowStock(false, auction.getSellerId());

            // then
            assertThat(auction.isShowStock()).isFalse();
        }

        @Test
        public void 동일한_판매자가_아닌_경우_가격_노출_정책을_변경할_수_없다() {
            // given
            Auction auction = AuctionFixture.createWaitingAuction();
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
            ZonedDateTime now = ZonedDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(1L)
                    .productName("상품이름")
                    .originPrice(10000)
                    .currentPrice(10000)
                    .originStock(999999)
                    .currentStock(999999)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now.plusHours(1L))
                    .finishedAt(now.plusHours(2L))
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
    public void updatePricePolicy_메소드는_경매의_가격_정책을_변경한다() {
        // given
        long sellerId = 1L;
        ZonedDateTime now = ZonedDateTime.now();
        Auction auction = Auction.builder()
                .sellerId(sellerId)
                .productName("상품이름")
                .originPrice(10000)
                .currentPrice(10000)
                .originStock(999999)
                .currentStock(999999)
                .pricePolicy(new ConstantPricePolicy(1000))
                .maximumPurchaseLimitCount(10)
                .variationDuration(Duration.ofMinutes(10L))
                .startedAt(now.minusHours(1L))
                .finishedAt(now)
                .isShowStock(true)
                .build();

        PricePolicy newPricePolicy = PricePolicy.createPercentagePricePolicy(7);

        // when
        auction.updatePricePolicy(newPricePolicy, sellerId);

        // then
        assertThat(auction.getPricePolicy()).isEqualTo(newPricePolicy);
    }

    @Test
    public void updatePricePolicy_메소드는_가격정책이_null이라면_변경이_무시된다() {
        // given
        long sellerId = 1L;
        ZonedDateTime now = ZonedDateTime.now();
        Auction auction = Auction.builder()
                .sellerId(sellerId)
                .productName("상품이름")
                .originPrice(10000)
                .originStock(999999)
                .currentStock(999999)
                .pricePolicy(new ConstantPricePolicy(1000))
                .maximumPurchaseLimitCount(10)
                .variationDuration(Duration.ofMinutes(10L))
                .startedAt(now.minusHours(1L))
                .finishedAt(now)
                .isShowStock(true)
                .build();

        // when
        auction.updatePricePolicy(null, sellerId);

        // then
        assertThat(auction.getPricePolicy()).isNotNull();
    }

    @Nested
    class changeStock_메소드는 {

        @Test
        void 변경할_재고가_1개_이상이라면_정상적으로_재고가_변경된다() {
            // given
            Auction auction = AuctionFixture.createWaitingAuction();

            // when
            auction.changeStock(1L, ZonedDateTime.now(), auction.getSellerId());

            // then
            assertThat(auction.getCurrentStock()).isEqualTo(1L);
        }

        @Test
        void 변경할_경매가_준비상태가_아니라면_예외가_발생한다() {
            // given
            Auction auction = AuctionFixture.createRunningAuction();
            ZonedDateTime now = ZonedDateTime.now();

            // expect
            assertThatThrownBy(() -> auction.changeStock(20, now, auction.getSellerId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(
                            String.format("시작 전인 경매의 재고만 수정할 수 있습니다. 시작시간=%s, 요청시간=%s", auction.getStartedAt(), now))
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A021);
        }

        @Test
        void 자신이_등록한_경매가_아니라면_예외가_발생한다() {
            // given
            Auction auction = AuctionFixture.createWaitingAuction();
            ZonedDateTime now = ZonedDateTime.now();

            // expect
            assertThatThrownBy(() -> auction.changeStock(0, now, auction.getSellerId() + 1L))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("자신이 등록한 경매만 수정할 수 있습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A018);
        }

        @Test
        void 변경할_재고가_1개_미만이면_예외가_발생한다() {
            // given
            Auction auction = AuctionFixture.createWaitingAuction();

            // expect
            assertThatThrownBy(() -> auction.changeStock(0, ZonedDateTime.now(), auction.getSellerId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("변경 할 재고는 1개 이상이어야 합니다. inputStock=0")
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A019);
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
                ZonedDateTime now = ZonedDateTime.now();
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
                ZonedDateTime now = ZonedDateTime.now();
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
                                ErrorCode.A022));
            }
        }

        @Nested
        class 만약_환불_후_재고가_원래_재고보다_많다면 {

            @Test
            void 예외가_발생한다() {
                // given
                long originStock = 999999L;
                long currentStock = 999999L;
                ZonedDateTime now = ZonedDateTime.now();
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
                                ErrorCode.A023));
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
                assertThatThrownBy(() -> auction.submit(2000, 10, ZonedDateTime.now()))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("진행 중인 경매에만 입찰할 수 있습니다. 현재상태: " + AuctionStatus.WAITING)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A016);
            }
        }

        @Nested
        class 요청시간_기준_현재_가격과_사용자가_요청한_가격이_다르다면 {

            @Test
            void 예외가_발생한다() {
                // given
                ZonedDateTime now = ZonedDateTime.now();
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
                ZonedDateTime requestTime = now.minusMinutes(30).plusMinutes(33);

                // expect
                assertThatThrownBy(() -> auction.submit(7001L, 10, requestTime))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format("입력한 가격으로 상품을 구매할 수 없습니다. 현재가격: %d 입력가격: %d", 7000L, 7001L))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A029);
            }
        }

        @Nested
        class 구매_불가능한_숫자만큼_요청이_오면 {

            @ParameterizedTest
            @ValueSource(longs = {0L, 31L, 101L})
            void 예외가_발생한다(long requestQuantity) {
                // given
                ZonedDateTime now = ZonedDateTime.now();
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
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A014);
            }
        }

        @Nested
        class 정상적인_구매요청이_들어오면 {

            @Test
            void 상품의_현재_재고가_차감된다() {
                // given
                ZonedDateTime now = ZonedDateTime.now();
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
                ZonedDateTime requestTime = now.minusMinutes(30).plusMinutes(33);

                // when
                auction.submit(7000L, 10, requestTime);

                // then
                assertThat(auction.getCurrentStock()).isEqualTo(90L);
            }
        }
    }
}

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
            assertThatThrownBy(() -> {
                Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(originPrice)
                        .stock(stock)
                        .pricePolicy(pricePolicy)
                        .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                        .variationDuration(varitationDuration)
                        .startedAt(ZonedDateTime.now().minusHours(1L))
                        .finishedAt(ZonedDateTime.now())
                        .isShowStock(true)
                        .build();
            }).isInstanceOf(BadRequestException.class)
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
                    .stock(999999)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(1L))
                    .startedAt(ZonedDateTime.now().minusHours(1L))
                    .finishedAt(ZonedDateTime.now())
                    .isShowStock(true)
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
                    .stock(999999)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .maximumPurchaseLimitCount(10)
                    .variationDuration(Duration.ofMinutes(1L))
                    .startedAt(ZonedDateTime.now().minusHours(1L))
                    .finishedAt(ZonedDateTime.now())
                    .isShowStock(true)
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
                    .stock(999999)
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
                .stock(999999)
                .pricePolicy(new ConstantPricePolicy(1000))
                .maximumPurchaseLimitCount(10)
                .variationDuration(Duration.ofMinutes(1L))
                .startedAt(ZonedDateTime.now().minusHours(1L))
                .finishedAt(ZonedDateTime.now())
                .isShowStock(true)
                .build();

        PricePolicy newPricePolicy = PricePolicy.percentagePricePolicy(7);

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
                .stock(999999)
                .pricePolicy(new ConstantPricePolicy(1000))
                .maximumPurchaseLimitCount(10)
                .variationDuration(Duration.ofMinutes(1L))
                .startedAt(ZonedDateTime.now().minusHours(1L))
                .finishedAt(ZonedDateTime.now())
                .isShowStock(true)
                .build();

        // when
        auction.updatePricePolicy(null, sellerId);

        // then
        assertThat(auction.getPricePolicy()).isNotNull();
    }
}
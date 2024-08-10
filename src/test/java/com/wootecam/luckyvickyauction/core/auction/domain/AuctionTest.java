package com.wootecam.luckyvickyauction.core.auction.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            PricePolicy pricePolicy = new ConstantPricePolicy();

            int variationWidth = 10000;
            Duration varitationDuration = Duration.ofMinutes(1L);

            // when & then
            assertThatThrownBy(() -> {
                Auction.builder()
                        .sellerId(1L)
                        .productName("상품이름")
                        .originPrice(originPrice)
                        .stock(stock)
                        .pricePolicy(pricePolicy)
                        .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                        .variationWidth(variationWidth)
                        .variationDuration(varitationDuration)
                        .startedAt(ZonedDateTime.now().minusHours(1L))
                        .finishedAt(ZonedDateTime.now())
                        .build();
            }).isInstanceOf(BadRequestException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A009);
        }
    }
}

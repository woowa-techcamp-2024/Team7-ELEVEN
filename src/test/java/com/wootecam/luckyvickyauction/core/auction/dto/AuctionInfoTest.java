package com.wootecam.luckyvickyauction.core.auction.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AuctionInfoTest {
    static Stream<Arguments> auctionInfoDtoArguments() {
        return Stream.of(
                Arguments.of("상품 이름은 비어있을 수 없습니다.",
                        ErrorCode.A001, 1L, 1L, "", 10000, 10000, 10, 10, Duration.ofMinutes(1L), ZonedDateTime.now(),
                        ZonedDateTime.now(), true),
                Arguments.of("상품 원가는 0보다 커야 합니다. 상품 원가: 0",
                        ErrorCode.A002, 1L, 1L, "상품이름", 0, 10000, 10, 10, Duration.ofMinutes(1L), ZonedDateTime.now(),
                        ZonedDateTime.now(), true),
                Arguments.of("현재 가격은 0보다 커야 합니다. 현재 가격: 0",
                        ErrorCode.A013, 1L, 1L, "상품이름", 10000, 0, 10, 10, Duration.ofMinutes(1L), ZonedDateTime.now(),
                        ZonedDateTime.now(), true),
                Arguments.of("재고는 0보다 작을 수 없습니다. 재고: -1",
                        ErrorCode.A000, 1L, 1L, "상품이름", 10000, 10000, -1, 10, Duration.ofMinutes(1L),
                        ZonedDateTime.now(), ZonedDateTime.now(), true),
                Arguments.of("최대 구매 수량 제한은 0보다 커야 합니다. 최대 구매 수량 제한: 0",
                        ErrorCode.A003, 1L, 1L, "상품이름", 10000, 10000, 10, 0, Duration.ofMinutes(1L),
                        ZonedDateTime.now(), ZonedDateTime.now(), true)
        );
    }

    @Test
    void 경매_정보_생성_요청을_정상적으로_처리한다() {
        // given
        Long auctionId = 1L;
        Long sellerId = 1L;
        String productName = "상품이름";
        long originPrice = 10000;
        long currentPrice = 10000;
        int stock = 10;
        int maximumPurchaseLimitCount = 10;

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        ZonedDateTime startedAt = ZonedDateTime.now().minusHours(1L);
        ZonedDateTime finishedAt = ZonedDateTime.now();

        // when
        AuctionInfo auctionInfo = new AuctionInfo(auctionId, sellerId, productName, originPrice, currentPrice, stock,
                maximumPurchaseLimitCount, pricePolicy, varitationDuration, startedAt, finishedAt, true);

        // then
        assertAll(
                () -> assertThat(auctionInfo.sellerId()).isEqualTo(sellerId),
                () -> assertThat(auctionInfo.productName()).isEqualTo(productName),
                () -> assertThat(auctionInfo.originPrice()).isEqualTo(originPrice),
                () -> assertThat(auctionInfo.currentPrice()).isEqualTo(currentPrice),
                () -> assertThat(auctionInfo.stock()).isEqualTo(stock),
                () -> assertThat(auctionInfo.maximumPurchaseLimitCount()).isEqualTo(maximumPurchaseLimitCount),
                () -> assertThat(auctionInfo.pricePolicy()).isEqualTo(pricePolicy),
                () -> assertThat(auctionInfo.variationDuration()).isEqualTo(varitationDuration),
                () -> assertThat(auctionInfo.startedAt()).isEqualTo(startedAt),
                () -> assertThat(auctionInfo.finishedAt()).isEqualTo(finishedAt),
                () -> assertThat(auctionInfo.isShowStock()).isTrue()
        );
    }

    @ParameterizedTest
    @MethodSource("auctionInfoDtoArguments")
    void 경매_정보_생성_요청이_잘못된_경우_예외가_발생한다(
            String expectedMessage,
            ErrorCode expectedErrorCode,
            Long auctionId,
            Long sellerId,
            String productName,
            long originPrice,
            long currentPrice,
            long stock,
            int maximumPurchaseLimitCount,
            Duration variationDuration,
            ZonedDateTime startedAt,
            ZonedDateTime finishedAt,
            boolean isShowStock
    ) {
        // expect
        assertThatThrownBy(() -> AuctionInfo.builder()
                .auctionId(auctionId)
                .sellerId(sellerId)
                .productName(productName)
                .originPrice(originPrice)
                .currentPrice(currentPrice)
                .stock(stock)
                .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                .pricePolicy(PricePolicy.createConstantPricePolicy(1000))
                .variationDuration(variationDuration)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .isShowStock(isShowStock)
                .build()
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(expectedMessage)
                .satisfies(exception -> {
                    assertThat(exception).hasFieldOrPropertyWithValue("errorCode", expectedErrorCode);
                });
    }
}

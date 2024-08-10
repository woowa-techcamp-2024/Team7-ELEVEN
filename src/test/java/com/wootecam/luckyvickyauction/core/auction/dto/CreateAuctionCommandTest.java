package com.wootecam.luckyvickyauction.core.auction.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CreateAuctionCommandTest {
    static Stream<Arguments> dtoArguments() {
        return Stream.of(
                Arguments.of("경매 재고는 인당 구매수량보다 작을 수 없다.", ErrorCode.A000,
                        1L, "상품이름", 10000, 1, 10,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("최대 구매 수량 제한은 0보다 커야한다.", ErrorCode.A003,
                        1L, "상품이름", 10000, 999999, 0,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("가격 변동폭은 0보다 커야한다.", ErrorCode.A004,
                        1L, "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(0),
                        0, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("변동 시간 단위는 0보다 커야한다.", ErrorCode.A005,
                        1L, "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000), 1000, Duration.ofMinutes(0L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("경매의 시작시간은 종료 시간보다 이전이어야한다.", ErrorCode.A006,
                        1L, "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now(), ZonedDateTime.now().minusSeconds(1)),
                Arguments.of("상품 이름은 비어있을 수 없다.", ErrorCode.A001,
                        1L, "", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("상품 원가는 0보다 커야한다.", ErrorCode.A002,
                        1L, "상품이름", 0, 999999, 10,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("경매 유형은 Null일 수 없다.", ErrorCode.A007,
                        1L, "상품이름", 10000, 999999, 10, null, 1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("sellerId는 Null일 수 없다.", ErrorCode.A007,
                        null, "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("상품 이름(productName)은 Null일 수 없다.", ErrorCode.A007,
                        1L, null, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("변동 주기(variationDuration)는 Null일 수 없다.", ErrorCode.A007,
                        1L, "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        1000, null,
                        ZonedDateTime.now().minusHours(1L), ZonedDateTime.now()),
                Arguments.of("시작 시간(startedAt)은 Null일 수 없다.", ErrorCode.A007,
                        1L, "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        null, ZonedDateTime.now()),
                Arguments.of("종료 시간(finishedAt)은 Null일 수 없다.", ErrorCode.A007,
                        1L, "상품이름", 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        1000, Duration.ofMinutes(1L),
                        ZonedDateTime.now().minusHours(1L), null)
        );
    }

    @Test
    @DisplayName("경매 생성 요청을 정상 생성한다.")
    void success_case() {
        // given
        Long sellerId = 1L;  // 판매자 정보
        String productName = "상품이름";
        int originPrice = 10000;
        int stock = 999999;  // 재고
        int maximumPurchaseLimitCount = 10;

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        ZonedDateTime startedAt = ZonedDateTime.of(2024, 8, 9, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));
        ZonedDateTime finishedAt = ZonedDateTime.of(2024, 8, 9, 1, 0, 0, 0, ZoneId.of("Asia/Seoul"));

        // expect
        assertThatNoException().isThrownBy(() -> new CreateAuctionCommand(
                sellerId, productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy, variationWidth,
                varitationDuration, startedAt, finishedAt, true
        ));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dtoArguments")
    @DisplayName("경매 생성 요청이 잘못된 경우 예외가 발생한다.")
    void validation_test(
            String displayName, ErrorCode expectedErrorCode,
            Long sellerId, String productName, int originPrice, int stock, int maximumPurchaseLimitCount,
            PricePolicy auctionType, long variationWidth, Duration varitationDuration,
            ZonedDateTime startedAt, ZonedDateTime finishedAt
    ) {
        // expect
        assertThatThrownBy(() -> new CreateAuctionCommand(
                sellerId, productName, originPrice, stock, maximumPurchaseLimitCount, auctionType, variationWidth,
                varitationDuration, startedAt, finishedAt, true))
                .isInstanceOf(BadRequestException.class)
                .satisfies(exception -> {
                    assertThat(exception).hasFieldOrPropertyWithValue("errorCode", expectedErrorCode);
                });
    }
}

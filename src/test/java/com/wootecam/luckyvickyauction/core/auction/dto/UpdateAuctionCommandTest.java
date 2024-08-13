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

class UpdateAuctionCommandTest {
    static Stream<Arguments> generateInvalidUpdateAuctionCommand() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        return Stream.of(
                Arguments.of("경매 재고는 인당 구매수량보다 작을 수 없다.", ErrorCode.A000,
                        1L, 1L, 10000, 1, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L),
                        now.plusHours(1L), now.plusHours(2L), now),
                Arguments.of("최대 구매 수량 제한은 0보다 커야한다.", ErrorCode.A003,
                        1L, 1L, 10000, 999999, 0,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L),
                        now.plusHours(1L), now.plusHours(2L), now),
                Arguments.of("변동 시간 단위는 0보다 커야한다.", ErrorCode.A005,
                        1L, 1L, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(0L),
                        now.plusHours(1L), now.plusHours(2L), now),
                Arguments.of("경매의 시작시간은 종료 시간보다 이전이어야한다.", ErrorCode.A006,
                        1L, 1L, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L),
                        now.plusHours(1L), now.plusHours(1L), now),
                Arguments.of("상품 원가는 0보다 커야한다.", ErrorCode.A002,
                        1L, 1L, 0, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L),
                        now.plusHours(1L), now.plusHours(2L), now),
                Arguments.of("경매 유형은 Null일 수 없다.", ErrorCode.A007,
                        1L, 1L, 10000, 999999, 10,
                        null, Duration.ofMinutes(1L),
                        now.plusHours(1L), now.plusHours(2L), now),
                Arguments.of("sellerId는 Null일 수 없다.", ErrorCode.A007,
                        1L, null, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L),
                        now.plusHours(1L), now.plusHours(2L), now),
                Arguments.of("변동 주기(variationDuration)는 Null일 수 없다.", ErrorCode.A007,
                        1L, 1L, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        null,
                        now.plusHours(1L), now.plusHours(2L), now),
                Arguments.of("시작 시간(startedAt)은 Null일 수 없다.", ErrorCode.A007,
                        1L, 1L, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L),
                        null, now.plusHours(2L), now),
                Arguments.of("종료 시간(finishedAt)은 Null일 수 없다.", ErrorCode.A007,
                        1L, 1L, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L),
                        now.plusHours(1L), null, now),
                Arguments.of("요청 시간(requestTime)은 Null일 수 없다.", ErrorCode.A007,
                        1L, 1L, 10000, 999999, 10,
                        new ConstantPricePolicy(1000),
                        Duration.ofMinutes(1L),
                        now.plusHours(1L), now.plusHours(2L), null)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("generateInvalidUpdateAuctionCommand")
    @DisplayName("경매 옵션 변경 요청이 잘못된 경우 예외가 발생한다.")
    void validation_test(
            String displayName, ErrorCode expectedErrorCode,
            Long auctionId, Long sellerId,
            int originPrice, int stock, int maximumPurchaseLimitCount,
            PricePolicy pricePolicy, Duration varitationDuration,
            ZonedDateTime startedAt, ZonedDateTime finishedAt, ZonedDateTime requestedAt
    ) {
        // expect
        assertThatThrownBy(() -> new UpdateAuctionCommand(
                auctionId, sellerId, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                varitationDuration, startedAt, finishedAt, true, requestedAt))
                .isInstanceOf(BadRequestException.class)
                .satisfies(
                        exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", expectedErrorCode));
    }

    @Test
    @DisplayName("경매 옵션 변경 요청을 정상 생성한다.")
    void success_case() {
        // given
        Long auctionId = 1L;  // 판매자 정보
        Long sellerId = 1L;  // 판매자 정보
        int originPrice = 10000;
        int stock = 999999;  // 재고
        int maximumPurchaseLimitCount = 10;

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        ZonedDateTime startedAt = ZonedDateTime.now().plusHours(1L);
        ZonedDateTime finishedAt = startedAt.plusHours(1L);

        // expect
        assertThatNoException().isThrownBy(() -> new UpdateAuctionCommand(
                auctionId, sellerId, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                varitationDuration, startedAt, finishedAt, true, ZonedDateTime.now()
        ));
    }

}

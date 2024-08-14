package com.wootecam.luckyvickyauction.core.auction.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionStatus;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.util.Mapper;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class BuyerAuctionInfoTest {
    static Stream<Arguments> auctionInfoDtoArguments() {
        return Stream.of(
                Arguments.of("상품 이름은 비어있을 수 없습니다.",
                        ErrorCode.A001, 1L, 1L, "", 10000, 10000, 10, 10, Duration.ofMinutes(1L), ZonedDateTime.now(),
                        ZonedDateTime.now(), true, AuctionStatus.WAITING),
                Arguments.of("상품 원가는 0보다 커야 합니다. 상품 원가: 0",
                        ErrorCode.A002, 1L, 1L, "상품이름", 0, 10000, 10, 10, Duration.ofMinutes(1L), ZonedDateTime.now(),
                        ZonedDateTime.now(), true, AuctionStatus.WAITING),
                Arguments.of("현재 가격은 0보다 커야 합니다. 현재 가격: 0",
                        ErrorCode.A013, 1L, 1L, "상품이름", 10000, 0, 10, 10, Duration.ofMinutes(1L), ZonedDateTime.now(),
                        ZonedDateTime.now(), true, AuctionStatus.WAITING),
                Arguments.of("최대 구매 수량 제한은 0보다 커야 합니다. 최대 구매 수량 제한: 0",
                        ErrorCode.A003, 1L, 1L, "상품이름", 10000, 10000, 10, 0, Duration.ofMinutes(1L),
                        ZonedDateTime.now(), ZonedDateTime.now(), true, AuctionStatus.WAITING)
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
        AuctionStatus status = AuctionStatus.WAITING;

        // when
        BuyerAuctionInfo buyerAuctionInfo = new BuyerAuctionInfo(auctionId, sellerId, productName, originPrice,
                currentPrice, stock,
                maximumPurchaseLimitCount, pricePolicy, varitationDuration, startedAt, finishedAt, true, status);

        // then
        assertAll(
                () -> assertThat(buyerAuctionInfo.auctionId()).isEqualTo(auctionId),
                () -> assertThat(buyerAuctionInfo.sellerId()).isEqualTo(sellerId),
                () -> assertThat(buyerAuctionInfo.productName()).isEqualTo(productName),
                () -> assertThat(buyerAuctionInfo.originPrice()).isEqualTo(originPrice),
                () -> assertThat(buyerAuctionInfo.currentPrice()).isEqualTo(currentPrice),
                () -> assertThat(buyerAuctionInfo.stock()).isEqualTo(stock),
                () -> assertThat(buyerAuctionInfo.maximumPurchaseLimitCount()).isEqualTo(maximumPurchaseLimitCount),
                () -> assertThat(buyerAuctionInfo.pricePolicy()).isEqualTo(pricePolicy),
                () -> assertThat(buyerAuctionInfo.variationDuration()).isEqualTo(varitationDuration),
                () -> assertThat(buyerAuctionInfo.startedAt()).isEqualTo(startedAt),
                () -> assertThat(buyerAuctionInfo.finishedAt()).isEqualTo(finishedAt),
                () -> assertThat(buyerAuctionInfo.isShowStock()).isTrue(),
                () -> assertThat(buyerAuctionInfo.status()).isEqualTo(status)
        );
    }

    @Test
    void 재고_노출이_비활성화된_경매는_재고를_0으로_처리한다() {
        // given
        Long auctionId = 1L;
        Long sellerId = 1L;
        String productName = "상품이름";
        long originPrice = 10000;
        int stock = 10;
        int maximumPurchaseLimitCount = 10;

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        ZonedDateTime startedAt = ZonedDateTime.now().minusHours(1L);
        ZonedDateTime finishedAt = ZonedDateTime.now();
        AuctionStatus status = AuctionStatus.WAITING;
        Auction auction = Auction.builder()
                .id(auctionId)
                .sellerId(sellerId)
                .productName(productName)
                .currentPrice(originPrice)
                .originPrice(originPrice)
                .originStock(stock)
                .currentStock(stock)
                .maximumPurchaseLimitCount(maximumPurchaseLimitCount)
                .pricePolicy(pricePolicy)
                .variationDuration(varitationDuration)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .isShowStock(false)
                .status(status)
                .build();

        // when
        BuyerAuctionInfo buyerAuctionInfo = Mapper.convertToBuyerAuctionInfo(auction);

        // then
        assertThat(buyerAuctionInfo.stock()).isEqualTo(0);
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
            boolean isShowStock,
            AuctionStatus status
    ) {
        // expect
        assertThatThrownBy(() -> BuyerAuctionInfo.builder()
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
                .status(status)
                .build()
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(expectedMessage)
                .satisfies(
                        exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", expectedErrorCode));
    }
}

package com.wootecam.luckyvickyauction.core.auction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.core.auction.domain.AuctionType;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantAuctionType;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuctionServiceTest {
    private AuctionService auctionService = new AuctionService();

    @Test
    @DisplayName("경매가 성공적으로 생성되면 예외가 발생하지 않는다.")
    void create_auction_success_case() {
        // given
        Long sellerId = 1L;  // 판매자 정보
        String productName = "상품이름";
        int originPrice = 10000;
        int stock = 999999;  // 재고
        int maximumPurchaseLimitCount = 10;
        AuctionType auctionType = new ConstantAuctionType();

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위

        ZonedDateTime startedAt = ZonedDateTime.of(2024, 8, 9, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));
        ZonedDateTime finishedAt = ZonedDateTime.of(2024, 8, 9, 1, 0, 0, 0, ZoneId.of("Asia/Seoul"));

        CreateAuctionCommand command = new CreateAuctionCommand(
                sellerId, productName, originPrice, stock, maximumPurchaseLimitCount, auctionType, variationWidth,
                varitationDuration, startedAt, finishedAt
        );

        // expect
        assertThatNoException().isThrownBy(() -> auctionService.createAuction(command));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 20, 30, 40, 50, 60})
    @DisplayName("경매의 지속시간은 최소 10분 최대 60분이다.")
    void auction_duration_should_be_between_10_and_60_minutes(int durationTime) {
        // given
        Long sellerId = 1L;  // 판매자 정보
        String productName = "상품이름";
        int originPrice = 10000;
        int stock = 999999;  // 재고
        int maximumPurchaseLimitCount = 10;
        AuctionType auctionType = new ConstantAuctionType();

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위

        ZonedDateTime startedAt = ZonedDateTime.of(2024, 8, 9, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));
        ZonedDateTime finishedAt = startedAt.plusMinutes(durationTime);

        CreateAuctionCommand command = new CreateAuctionCommand(
            sellerId, productName, originPrice, stock, maximumPurchaseLimitCount, auctionType, variationWidth,
            varitationDuration, startedAt, finishedAt
        );

        // expect
        assertThatNoException().isThrownBy(() -> auctionService.createAuction(command));
    }

}

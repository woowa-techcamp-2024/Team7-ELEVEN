package com.wootecam.luckyvickyauction.core.auction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {
    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private AuctionService auctionService;

    @Test
    @DisplayName("경매가 성공적으로 생성된다.")
    void create_auction_success_case() {
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

        CreateAuctionCommand command = new CreateAuctionCommand(
                sellerId, productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                varitationDuration, startedAt, finishedAt, true
        );

        // when
        auctionService.createAuction(command);

        // then
        verify(auctionRepository).save(any(Auction.class));
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

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        ZonedDateTime startedAt = ZonedDateTime.of(2024, 8, 9, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));
        ZonedDateTime finishedAt = startedAt.plusMinutes(durationTime);

        CreateAuctionCommand command = new CreateAuctionCommand(
                sellerId, productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                varitationDuration, startedAt, finishedAt, true
        );

        // expect
        assertThatNoException().isThrownBy(() -> auctionService.createAuction(command));
    }

    @ParameterizedTest(name = "경매 지속시간이 {0}인 경우 BadRequestException 예외가 발생하고 에러 코드는 A008이다.")
    @ValueSource(ints = {9, 11, 19, 21, 29, 31, 39, 41, 49, 51, 59, 61})
    @DisplayName("경매의 지속시간이 10, 20, 30, 40, 50, 60이 아닌 경우 예외가 발생한다.")
    void auction_duration_should_be_between_10_and_60_minutes_fail(long durationTime) {
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
        ZonedDateTime finishedAt = startedAt.plusMinutes(durationTime);

        CreateAuctionCommand command = new CreateAuctionCommand(
                sellerId, productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                varitationDuration, startedAt, finishedAt, true
        );

        // expect
        assertThatThrownBy(() -> auctionService.createAuction(command))
                .isInstanceOf(BadRequestException.class)
                .satisfies(exception -> {
                    assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.A008);
                });
    }

    @Nested
    @DisplayName("changeOption test")
    class changeOption {

        @Test
        @DisplayName("경매 ID에 해당하는 경매를 찾을 수 없는 경우 예외가 발생하고 에러 코드는 A011이다.")
        void notFound_should_throw_exception() {
            // given
            Long auctionId = 1L;  // 경매 정보
            int originPrice = 10000;
            int stock = 999999;  // 재고
            int maximumPurchaseLimitCount = 10;

            int variationWidth = 1000;
            Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
            PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

            ZonedDateTime startedAt = ZonedDateTime.of(2024, 8, 9, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));
            ZonedDateTime finishedAt = ZonedDateTime.of(2024, 8, 9, 1, 0, 0, 0, ZoneId.of("Asia/Seoul"));

            final UpdateAuctionCommand updateAuctionCommand = new UpdateAuctionCommand(
                    auctionId, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                    varitationDuration, startedAt, finishedAt, true, ZonedDateTime.now()
            );

            // when
            when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> auctionService.changeOption(updateAuctionCommand))
                    .isInstanceOf(NotFoundException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A011));
        }

        @Test
        @Disabled
        @DisplayName("이미 시작한 경매를 변경하려는 경우 예외가 발생하고 에러 코드는 A012이다.")
        void when_change_auction_that_is_started() {

            // given
            Long auctionId = 1L;  // 경매 정보
            int originPrice = 10000;
            int stock = 999999;  // 재고
            int maximumPurchaseLimitCount = 10;

            int variationWidth = 1000;
            Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
            PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

            ZonedDateTime startedAt = ZonedDateTime.of(2024, 8, 9, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"));
            ZonedDateTime finishedAt = ZonedDateTime.of(2024, 8, 9, 1, 0, 0, 0, ZoneId.of("Asia/Seoul"));
            ZonedDateTime requestTime = ZonedDateTime.of(2024, 8, 9, 2, 0, 0, 0, ZoneId.of("Asia/Seoul"));

            final UpdateAuctionCommand updateAuctionCommand = new UpdateAuctionCommand(
                    auctionId, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                    varitationDuration, startedAt, finishedAt, true, requestTime
            );

            Auction auction = Auction.builder()
                    .startedAt(startedAt)
                    .finishedAt(finishedAt)
                    .sellerId(1L)
                    .productName("Test Product")
                    .originPrice(10000)
                    .stock(999999)
                    .maximumPurchaseLimitCount(10)
                    .pricePolicy(new ConstantPricePolicy(1000))
                    .variationDuration(Duration.ofMinutes(1L))
                    .isShowStock(true)
                    .build();

            // when
            when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

            // then
            assertThatThrownBy(() -> auctionService.changeOption(updateAuctionCommand))
                    .isInstanceOf(NotFoundException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A012));
        }
    }

    @Nested
    @DisplayName("경매 입찰(구매)을 진행할 때")
    class submitBidTest {
        @Test
        @DisplayName("요청을 정상적으로 처리한다.")
        void success_case() {
            // given
            long auctionId = 1L;
            long price = 10000L;
            long quantity = 100L;

            Auction auction = Auction.builder()
                .startedAt(ZonedDateTime.now())
                .finishedAt(ZonedDateTime.now().plusMinutes(10))
                .sellerId(1L)
                .productName("Test Product")
                .originPrice(10000)
                .stock(100)
                .maximumPurchaseLimitCount(100)
                .pricePolicy(new ConstantPricePolicy(1000))
                .variationDuration(Duration.ofMinutes(1L))
                .isShowStock(true)
                .build();

            // when
            when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

            // then
            assertThatNoException().isThrownBy(() -> auctionService.submitBid(auctionId, price, quantity));
        }

        @Test
        @DisplayName("유효하지 않은 경매번호를 전달받은 경우 예외가 발생하고 에러 코드는 A011이다.")
        void when_invalid_auction_id_should_throw_exception() {
            // given
            long auctionId = 1L;
            long price = 10000L;
            long quantity = 100L;

            // when
            when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> auctionService.submitBid(auctionId, price, quantity))
                .isInstanceOf(NotFoundException.class)
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.A011));
        }

        @Test
        @DisplayName("경매 재고보다 많은 수량을 입찰(구매)하려는 경우 예외가 발생하고 에러 코드는 A014이다.")
        void when_quantity_more_than_auction_stock_should_throw_exception() {
            // given
            long auctionId = 1L;
            long price = 10000L;
            long quantity = 100L;

            Auction auction = Auction.builder()
                .startedAt(ZonedDateTime.now())
                .finishedAt(ZonedDateTime.now().plusMinutes(10))
                .sellerId(1L)
                .productName("Test Product")
                .originPrice(10000)
                .stock(50)  // 경매 재고
                .maximumPurchaseLimitCount(50)
                .pricePolicy(new ConstantPricePolicy(1000))
                .variationDuration(Duration.ofMinutes(1L))
                .isShowStock(true)
                .build();

            // when
            when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

            // then
            assertThatThrownBy(() -> auctionService.submitBid(auctionId, price, quantity))
                .isInstanceOf(BadRequestException.class)
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.A014));
        }

        @Test
        @DisplayName("인당 구매 제한 수량보다 많은 수량을 입찰(구매)하려는 경우 예외가 발생하고 에러 코드는 A014이다.")
        void when_quantity_more_than_maximum_pruchase_limit_should_throw_exception() {
            // given
            long auctionId = 1L;
            long price = 10000L;
            long quantity = 100L;

            Auction auction = Auction.builder()
                .startedAt(ZonedDateTime.now())
                .finishedAt(ZonedDateTime.now().plusMinutes(10))
                .sellerId(1L)
                .productName("Test Product")
                .originPrice(10000)
                .stock(10000)
                .maximumPurchaseLimitCount(10)  // 인당 구매 수량 제한
                .pricePolicy(new ConstantPricePolicy(1000))
                .variationDuration(Duration.ofMinutes(1L))
                .isShowStock(true)
                .build();

            // when
            when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

            // then
            assertThatThrownBy(() -> auctionService.submitBid(auctionId, price, quantity))
                .isInstanceOf(BadRequestException.class)
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.A014));
        }
    }
}

package com.wootecam.luckyvickyauction.core.auction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionStatus;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionStockCommand;
import com.wootecam.luckyvickyauction.core.auction.fixture.AuctionFixture;
import com.wootecam.luckyvickyauction.core.auction.infra.FakeAuctionRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuctionServiceTest {

    private AuctionRepository auctionRepository;
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionRepository = new FakeAuctionRepository();
        auctionService = new AuctionService(auctionRepository);
    }

    @Test
    @DisplayName("경매가 성공적으로 생성된다.")
    void create_auction_success_case() {
        // given
        Long sellerId = 1L;  // 판매자 정보
        String productName = "상품이름";
        long originPrice = 10000;
        long stock = 999999;  // 재고
        long maximumPurchaseLimitCount = 10;

        int variationWidth = 1000;
        Duration varitationDuration = Duration.ofMinutes(10L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        ZonedDateTime startedAt = ZonedDateTime.now().plusHours(1);
        ZonedDateTime finishedAt = startedAt.plusHours(1);

        SignInInfo sellerInfo = new SignInInfo(sellerId, Role.SELLER);
        CreateAuctionCommand command = new CreateAuctionCommand(productName, originPrice, stock,
                maximumPurchaseLimitCount, pricePolicy, varitationDuration, ZonedDateTime.now(), startedAt, finishedAt,
                true
        );

        // when
        auctionService.createAuction(sellerInfo, command);
        Auction createdAuction = auctionRepository.findById(1L).get();

        // then
        assertAll(
                () -> assertThat(createdAuction.getSellerId()).isEqualTo(sellerId),
                () -> assertThat(createdAuction.getProductName()).isEqualTo(productName),
                () -> assertThat(createdAuction.getOriginPrice()).isEqualTo(originPrice),
                () -> assertThat(createdAuction.getCurrentPrice()).isEqualTo(originPrice),
                () -> assertThat(createdAuction.getOriginStock()).isEqualTo(stock),
                () -> assertThat(createdAuction.getCurrentStock()).isEqualTo(stock),
                () -> assertThat(createdAuction.getMaximumPurchaseLimitCount()).isEqualTo(maximumPurchaseLimitCount),
                () -> assertThat(createdAuction.getPricePolicy()).isEqualTo(pricePolicy),
                () -> assertThat(createdAuction.getVariationDuration()).isEqualTo(varitationDuration),
                () -> assertThat(createdAuction.getStartedAt()).isEqualTo(startedAt),
                () -> assertThat(createdAuction.getFinishedAt()).isEqualTo(finishedAt),
                () -> assertThat(createdAuction.isShowStock()).isTrue(),
                () -> assertThat(createdAuction.getId()).isNotNull()
        );
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
        Duration varitationDuration = Duration.ofMinutes(10L);  // 변동 시간 단위
        PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

        ZonedDateTime startedAt = ZonedDateTime.now().plusHours(1);
        ZonedDateTime finishedAt = startedAt.plusMinutes(durationTime);

        SignInInfo sellerInfo = new SignInInfo(sellerId, Role.SELLER);
        CreateAuctionCommand command = new CreateAuctionCommand(
                productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                varitationDuration, ZonedDateTime.now(), startedAt, finishedAt, true
        );

        // expect
        assertThatNoException().isThrownBy(() -> auctionService.createAuction(sellerInfo, command));
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

        ZonedDateTime startedAt = ZonedDateTime.now().plusHours(1);
        ZonedDateTime finishedAt = startedAt.plusMinutes(durationTime);

        SignInInfo sellerInfo = new SignInInfo(sellerId, Role.SELLER);
        CreateAuctionCommand command = new CreateAuctionCommand(
                productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                varitationDuration, ZonedDateTime.now(), startedAt, finishedAt, true
        );

        // expect
        assertThatThrownBy(() -> auctionService.createAuction(sellerInfo, command))
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
            Long sellerId = 1L;
            int originPrice = 10000;
            int stock = 999999;  // 재고
            int maximumPurchaseLimitCount = 10;

            int variationWidth = 1000;
            Duration varitationDuration = Duration.ofMinutes(1L);  // 변동 시간 단위
            PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

            ZonedDateTime startedAt = ZonedDateTime.now().plusHours(1L);
            ZonedDateTime finishedAt = startedAt.plusHours(1L);

            final UpdateAuctionCommand updateAuctionCommand = new UpdateAuctionCommand(
                    auctionId, sellerId, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                    varitationDuration, startedAt, finishedAt, true, ZonedDateTime.now()
            );

            // expect
            assertThatThrownBy(() -> auctionService.changeOption(updateAuctionCommand))
                    .isInstanceOf(NotFoundException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A011));
        }

        @Test
        @DisplayName("이미 시작한 경매를 변경하려는 경우 예외가 발생하고 에러 코드는 A012이다.")
        void when_change_auction_that_is_started() {
            // given
            Auction runningAuction = saveRunningAuction();
            UpdateAuctionCommand updateAuctionCommand = new UpdateAuctionCommand(
                    runningAuction.getId(),
                    runningAuction.getSellerId(),
                    runningAuction.getOriginPrice(),
                    80L,
                    runningAuction.getMaximumPurchaseLimitCount(),
                    runningAuction.getPricePolicy(),
                    runningAuction.getVariationDuration(),
                    ZonedDateTime.now().plusHours(1L),
                    ZonedDateTime.now().plusHours(2L),
                    true,
                    ZonedDateTime.now()
            );

            // expect
            assertThatThrownBy(() -> auctionService.changeOption(updateAuctionCommand))
                    .isInstanceOf(BadRequestException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A012));
        }
    }

    @Nested
    class submitBid_메소드는 {

        @Nested
        class 정상적인_구매요청이_들어오면 {

            @Test
            void 구매를_완료한다() {
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
                Auction savedAuction = auctionRepository.save(auction);

                // when
                auctionService.submitBid(savedAuction.getId(), 7000L, 10, now);

                // then
                Auction afterAuction = auctionRepository.findById(savedAuction.getId()).get();
                assertThat(afterAuction.getCurrentStock()).isEqualTo(90L);
            }
        }

        @Nested
        class 존재하지_않는_경매의_id가_입력된_경우 {

            @Test
            void 예외가_발생한다() {
                // expect
                assertThatThrownBy(() -> auctionService.submitBid(1L, 1000L, 10L, ZonedDateTime.now()))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("경매(Auction)를 찾을 수 없습니다. AuctionId: 1")
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A011);
            }
        }

        @Nested
        class 현재_경매_재고보다_많이_구매하려_하면 {

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
                Auction savedAuction = auctionRepository.save(auction);

                // expect
                assertThatThrownBy(() -> auctionService.submitBid(savedAuction.getId(), 7000L, 101, now))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format("해당 수량만큼 구매할 수 없습니다. 재고: %d, 요청: %d, 인당구매제한: %d", 100L, 101L, 10L));
            }
        }

        @Nested
        class 진행_중인_경매가_아닌_경우 {

            @Test
            void 예외가_발생한다() {
                // given
                ZonedDateTime now = ZonedDateTime.now();
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("Test Product")
                        .originPrice(10000)
                        .currentPrice(10000)
                        .originStock(100)
                        .currentStock(100)
                        .maximumPurchaseLimitCount(100)
                        .pricePolicy(new ConstantPricePolicy(1000))
                        .startedAt(now.plusHours(1L))
                        .finishedAt(now.plusHours(2L))
                        .variationDuration(Duration.ofMinutes(10L))
                        .isShowStock(true)
                        .build();
                Auction savedAuction = auctionRepository.save(auction);

                // expect
                assertThatThrownBy(
                        () -> auctionService.submitBid(savedAuction.getId(), 7000L, 10L, ZonedDateTime.now()))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("진행 중인 경매에만 입찰할 수 있습니다. 현재상태: " + AuctionStatus.WAITING)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A016);
            }
        }
    }

    @Nested
    class changeStock_메소드는 {

        @Nested
        class 만약_요청한_사용자가_판매자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Auction auction = saveWaitingAuction();
                ZonedDateTime now = ZonedDateTime.now();

                // expect
                assertThatThrownBy(
                        () -> auctionService.changeStock(new SignInInfo(10L, Role.BUYER),
                                new UpdateAuctionStockCommand(now, auction.getId(), 50L)))
                        .isInstanceOf(UnauthorizedException.class)
                        .hasMessage("판매자만 재고를 수정할 수 있습니다.")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.A017));
            }
        }

        @Nested
        class 만약_요청한_경매의_상태가_시작전이_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Auction auction = saveRunningAuction();
                ZonedDateTime now = ZonedDateTime.now();

                // expect
                String message = String.format("시작 전인 경매의 재고만 수정할 수 있습니다. 시작시간=%s, 요청시간=%s", auction.getStartedAt(),
                        now);
                assertThatThrownBy(
                        () -> auctionService.changeStock(new SignInInfo(auction.getSellerId(), Role.SELLER),
                                new UpdateAuctionStockCommand(now, auction.getId(), 50L)))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(message)
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.A021));
            }
        }

        @Nested
        class 만약_요청_판매자와_경매를_만든_판매자가_다르다면 {

            @Test
            void 예외가_발생한다() {
                // given
                Auction auction = saveWaitingAuction();
                ZonedDateTime now = ZonedDateTime.now();

                // expect
                assertThatThrownBy(
                        () -> auctionService.changeStock(new SignInInfo(auction.getSellerId() + 1L, Role.SELLER),
                                new UpdateAuctionStockCommand(now, auction.getId(), 50)))
                        .isInstanceOf(UnauthorizedException.class)
                        .hasMessage("자신이 등록한 경매만 수정할 수 있습니다.")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.A018));
            }
        }

        @Nested
        class 정상적인_재고_변경_요청이_오면 {

            @Test
            void 재고를_변경한다() {
                // given
                Auction auction = saveWaitingAuction();
                ZonedDateTime now = ZonedDateTime.now();

                // when
                auctionService.changeStock(new SignInInfo(auction.getId(), Role.SELLER),
                        new UpdateAuctionStockCommand(now, auction.getId(), 50L));
                Auction updatedAuction = auctionRepository.findById(auction.getId()).get();

                // then
                assertThat(updatedAuction.getCurrentStock()).isEqualTo(50L);
            }
        }
    }

    @Nested
    class cancelBid_메소드는 {

        @Nested
        class 정상적인_입찰_취소_요청이_오면 {

            @Test
            void 입찰을_취소한다() {
                // given
                ZonedDateTime now = ZonedDateTime.now();
                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("productName")
                        .originPrice(10000L)
                        .currentPrice(10000L)
                        .originStock(100L)
                        .currentStock(50L)
                        .maximumPurchaseLimitCount(10L)
                        .pricePolicy(new ConstantPricePolicy(1000L))
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusMinutes(30))
                        .finishedAt(now.plusMinutes(30))
                        .isShowStock(true)
                        .build();
                auction = auctionRepository.save(auction);

                // when
                auctionService.cancelBid(auction.getId(), 50L);
                Auction updatedAuction = auctionRepository.findById(auction.getId()).get();

                // then
                assertThat(updatedAuction.getCurrentStock()).isEqualTo(100L);
            }
        }
    }

    @Nested
    class getSellerAuction_메소드는 extends GetSellerAuctionTest {

    }

    @Nested
    class cancelAuction_메소드는 extends CancelAuctionTest {

    }

    /**
     * 현재 RUNNING 상태인 Auction을 생성 및 Repository에 저장하고 반환합니다.
     *
     * @return 저장된 Auction 반환
     */
    private Auction saveRunningAuction() {
        Auction auction = AuctionFixture.createRunningAuction();
        return auctionRepository.save(auction);
    }

    private Auction saveWaitingAuction() {
        Auction auction = AuctionFixture.createWaitingAuction();
        return auctionRepository.save(auction);
    }
}

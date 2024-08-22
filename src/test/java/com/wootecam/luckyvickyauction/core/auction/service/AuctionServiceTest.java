package com.wootecam.luckyvickyauction.core.auction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.context.ServiceTest;
import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionStatus;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.CancelAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.fixture.AuctionFixture;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.global.exception.AuthorizationException;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class AuctionServiceTest extends ServiceTest {

    @Nested
    class createAuction_메소드는 {

        @Nested
        class 정상적인_요청이_들어오면 {

            @Test
            void 경매가_생성된다() {
                // given
                Long sellerId = 1L;  // 판매자 정보
                String productName = "상품이름";
                long originPrice = 10000;
                long stock = 999999;  // 재고
                long maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                Duration varitationDuration = Duration.ofMinutes(10L);  // 변동 시간 단위
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

                LocalDateTime startedAt = LocalDateTime.now().plusHours(1);
                LocalDateTime finishedAt = startedAt.plusHours(1);

                SignInInfo sellerInfo = new SignInInfo(sellerId, Role.SELLER);
                CreateAuctionCommand command = new CreateAuctionCommand(productName, originPrice, stock,
                        maximumPurchaseLimitCount, pricePolicy, varitationDuration, LocalDateTime.now(), startedAt,
                        finishedAt,
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
                        () -> assertThat(createdAuction.getMaximumPurchaseLimitCount()).isEqualTo(
                                maximumPurchaseLimitCount),
                        () -> assertThat(createdAuction.getPricePolicy()).isEqualTo(pricePolicy),
                        () -> assertThat(createdAuction.getVariationDuration()).isEqualTo(varitationDuration),
                        () -> assertThat(createdAuction.getStartedAt()).isEqualTo(startedAt),
                        () -> assertThat(createdAuction.getFinishedAt()).isEqualTo(finishedAt),
                        () -> assertThat(createdAuction.isShowStock()).isTrue(),
                        () -> assertThat(createdAuction.getId()).isNotNull()
                );
            }
        }

        @Nested
        class 경매의_지속시간이_최소10분_최대60분이면 {

            @ParameterizedTest
            @ValueSource(ints = {10, 20, 30, 40, 50, 60})
            void 경매가_생성된다(int durationTime) {
                // given
                Long sellerId = 1L;
                String productName = "상품이름";
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                Duration varitationDuration = Duration.ofMinutes(10L);
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

                LocalDateTime startedAt = LocalDateTime.now().plusHours(1);
                LocalDateTime finishedAt = startedAt.plusMinutes(durationTime);

                SignInInfo sellerInfo = new SignInInfo(sellerId, Role.SELLER);
                CreateAuctionCommand command = new CreateAuctionCommand(
                        productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                        varitationDuration, LocalDateTime.now(), startedAt, finishedAt, true
                );

                // expect
                assertThatNoException().isThrownBy(() -> auctionService.createAuction(sellerInfo, command));
            }
        }

        @Nested
        class 만약_경매_지속시간이_10분_단위가_아니라면 {

            @ParameterizedTest
            @ValueSource(ints = {9, 11, 19, 21, 29, 31, 39, 41, 49, 51, 59, 61})
            void 예외가_발생한다(int invalidDurationTime) {
                // given
                Long sellerId = 1L;
                String productName = "상품이름";
                int originPrice = 10000;
                int stock = 999999;
                int maximumPurchaseLimitCount = 10;

                int variationWidth = 1000;
                Duration varitationDuration = Duration.ofMinutes(1L);
                PricePolicy pricePolicy = new ConstantPricePolicy(variationWidth);

                LocalDateTime startedAt = LocalDateTime.now().plusHours(1);
                LocalDateTime finishedAt = startedAt.plusMinutes(invalidDurationTime);

                SignInInfo sellerInfo = new SignInInfo(sellerId, Role.SELLER);
                CreateAuctionCommand command = new CreateAuctionCommand(
                        productName, originPrice, stock, maximumPurchaseLimitCount, pricePolicy,
                        varitationDuration, LocalDateTime.now(), startedAt, finishedAt, true
                );

                // expect
                assertThatThrownBy(() -> auctionService.createAuction(sellerInfo, command))
                        .isInstanceOf(BadRequestException.class)
                        .satisfies(exception -> {
                            assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.A007);
                        });
            }
        }
    }

    @Nested
    class submitPurchase_메소드는 {

        @Nested
        class 정상적인_구매요청이_들어오면 {

            @Test
            void 구매를_완료한다() {
                // given
                LocalDateTime now = LocalDateTime.now();
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
                auctionService.submitPurchase(savedAuction.getId(), 7000L, 10, now);

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
                assertThatThrownBy(() -> auctionService.submitPurchase(1L, 1000L, 10L, LocalDateTime.now()))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("경매(Auction)를 찾을 수 없습니다. AuctionId: 1")
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A010);
            }
        }

        @Nested
        class 현재_경매_재고보다_많이_구매하려_하면 {

            @Test
            void 예외가_발생한다() {
                // given
                LocalDateTime now = LocalDateTime.now();
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
                assertThatThrownBy(() -> auctionService.submitPurchase(savedAuction.getId(), 7000L, 101, now))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format("해당 수량만큼 구매할 수 없습니다. 재고: %d, 요청: %d, 인당구매제한: %d", 100L, 101L, 10L));
            }
        }

        @Nested
        class 진행_중인_경매가_아닌_경우 {

            @Test
            void 예외가_발생한다() {
                // given
                LocalDateTime now = LocalDateTime.now();
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
                        () -> auctionService.submitPurchase(savedAuction.getId(), 7000L, 10L, LocalDateTime.now()))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("진행 중인 경매에만 입찰할 수 있습니다. 현재상태: " + AuctionStatus.WAITING)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A013);
            }
        }
    }

    @Nested
    class cancelPurchase_메소드는 {

        @Nested
        class 정상적인_입찰_취소_요청이_오면 {

            @Test
            void 입찰을_취소한다() {
                // given
                LocalDateTime now = LocalDateTime.now();
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
                auctionService.cancelPurchase(auction.getId(), 50L);
                Auction updatedAuction = auctionRepository.findById(auction.getId()).get();

                // then
                assertThat(updatedAuction.getCurrentStock()).isEqualTo(100L);
            }
        }

        @Nested
        class 환불_할_재고가_1개_미만이라면 {

            @Test
            void 예외가_발생한다() {
                // given
                LocalDateTime now = LocalDateTime.now();
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
                Auction savedAuction = auctionRepository.save(auction);

                // expect
                assertThatThrownBy(() -> auctionService.cancelPurchase(savedAuction.getId(), 0))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("환불할 재고는 1보다 작을 수 없습니다. inputStock=0")
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A015);
            }
        }

        @Nested
        class 환불_후_재고가_원래_재고보다_많다면 {

            @Test
            void 예외가_발생한다() {
                // given
                LocalDateTime now = LocalDateTime.now();
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
                Auction savedAuction = auctionRepository.save(auction);

                // expect
                assertThatThrownBy(() -> auctionService.cancelPurchase(savedAuction.getId(), 60L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("환불 후 재고는 원래 재고보다 많을 수 없습니다. inputStock=60")
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A016);
            }
        }
    }

    @Nested
    class getSellerAuction_메소드는 {

        @Nested
        class 정상적인_요청이_오면 {

            @Test
            void 판매자의_경매목록을_반환한다() {
                // given
                Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());

                LocalDateTime now = LocalDateTime.now();
                Auction action = Auction.builder()
                        .sellerId(seller.getId())
                        .productName("productName")
                        .originPrice(10000L)
                        .currentPrice(10000L)
                        .originStock(100L)
                        .currentStock(100L)
                        .maximumPurchaseLimitCount(10L)
                        .pricePolicy(new ConstantPricePolicy(1000L))
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.plusHours(1))
                        .finishedAt(now.plusHours(2))
                        .isShowStock(true)
                        .build();

                Auction auction = auctionRepository.save(action);

                SignInInfo signInInfo = new SignInInfo(seller.getId(), seller.getRole());

                // when
                SellerAuctionInfo sellerAuctionInfo = auctionService.getSellerAuction(signInInfo, auction.getId());

                // then
                assertAll(
                        () -> assertThat(sellerAuctionInfo.auctionId()).isEqualTo(auction.getId())
                );
            }
        }

        @Nested
        class 요철한_판매자가_경매의_판매자가_아닌_경우 {

            @Test
            void 예외가_발생한다() {
                // given
                Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());
                Auction auction = auctionRepository.save(AuctionFixture.createWaitingAuction());
                SignInInfo signInInfo = new SignInInfo(seller.getId() + 1, seller.getRole());

                // expect
                AssertionsForClassTypes.assertThatThrownBy(
                                () -> auctionService.getSellerAuction(signInInfo, auction.getId()))
                        .isInstanceOf(AuthorizationException.class)
                        .hasMessage("판매자는 자신이 등록한 경매만 조회할 수 있습니다.")
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A020);
            }
        }
    }

    @Nested
    class cancelAuction_메소드는 {

        @Test
        void 정상적으로_취소되어_경매가_삭제된다() {
            // given
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            CancelAuctionCommand command = new CancelAuctionCommand(LocalDateTime.now(), 1L);
            Auction auction = AuctionFixture.createWaitingAuction();
            auctionRepository.save(auction);

            // when
            auctionService.cancelAuction(signInInfo, command);
            Optional<Auction> foundAuction = auctionRepository.findById(1L);

            // then
            assertThat(foundAuction).isNotPresent();
        }

        static Stream<Arguments> generateInvalidAuction() {
            return Stream.of(
                    Arguments.of("진행중인 경매는 취소할 수 없다.", AuctionFixture.createRunningAuction()),
                    Arguments.of("종료된 경매는 취소할 수 없다.", AuctionFixture.createFinishedAuction())
            );
        }

        @Test
        void 판매자가_권한이_없는_사용자_접근시_예외가_발생한다() {
            // given
            SignInInfo signInInfo = new SignInInfo(1L, Role.BUYER);
            CancelAuctionCommand command = new CancelAuctionCommand(LocalDateTime.now(), 1L);

            // expect
            assertThatThrownBy(
                    () -> auctionService.cancelAuction(signInInfo, command))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage("판매자만 경매를 취소할 수 있습니다.")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A017));
        }

        @Test
        void 경매를_등록한_판매자와_경매를_수정하려는_판매자가_다른_경우_예외가_발생한다() {
            // given
            SignInInfo signInInfo = new SignInInfo(2L, Role.SELLER);
            CancelAuctionCommand command = new CancelAuctionCommand(LocalDateTime.now(), 1L);
            Auction auction = AuctionFixture.createWaitingAuction();
            auctionRepository.save(auction);

            // expect
            assertThatThrownBy(
                    () -> auctionService.cancelAuction(signInInfo, command))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage("자신이 등록한 경매만 취소할 수 있습니다.")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A018));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("generateInvalidAuction")
        void 경매_상태가_시작전이_아닌경우_예외가_발생한다(String displayName, Auction auction) {
            // given
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            CancelAuctionCommand command = new CancelAuctionCommand(LocalDateTime.now(), 1L);

            auctionRepository.save(auction);

            // expect
            assertThatThrownBy(
                    () -> auctionService.cancelAuction(signInInfo, command))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageStartingWith("시작 전인 경매만 취소할 수 있습니다.")
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.A019));
        }
    }
}

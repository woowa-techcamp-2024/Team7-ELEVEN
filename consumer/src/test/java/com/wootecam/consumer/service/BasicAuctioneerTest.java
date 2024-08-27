package com.wootecam.consumer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.consumer.context.DatabaseCleaner;
import com.wootecam.core.domain.entity.Auction;
import com.wootecam.core.domain.entity.Member;
import com.wootecam.core.domain.entity.Point;
import com.wootecam.core.domain.entity.Receipt;
import com.wootecam.core.domain.entity.type.ConstantPricePolicy;
import com.wootecam.core.domain.entity.type.ReceiptStatus;
import com.wootecam.core.domain.entity.type.Role;
import com.wootecam.core.domain.repository.AuctionRepository;
import com.wootecam.core.domain.repository.MemberRepository;
import com.wootecam.core.domain.repository.ReceiptRepository;
import com.wootecam.core.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.core.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.exception.AuthorizationException;
import com.wootecam.core.exception.BadRequestException;
import com.wootecam.core.exception.ErrorCode;
import com.wootecam.core.exception.NotFoundException;
import com.wootecam.core.service.auctioneer.Auctioneer;
import com.wootecam.test.context.ServiceTest;
import com.wootecam.test.fixture.AuctionFixture;
import com.wootecam.test.fixture.MemberFixture;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BasicAuctioneerTest extends ServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AuctionRepository auctionRepository;
    @Autowired
    ReceiptRepository receiptRepository;
    @Autowired
    Auctioneer auctioneer;
    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void tearDown(){
        databaseCleaner.clear();
    }

    @Nested
    class 동시성_테스트 {

        @Nested
        class 재고보다_많은_구매_요청이_들어오면 {

            @RepeatedTest(10)
            void 재고_수_만큼만_판매해야_한다() throws InterruptedException {
                // given
                Member seller = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
                Member buyer = memberRepository.save(Member.builder()
                        .signInId("buyerId")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(1000000000L))
                        .build());

                Auction auction = auctionRepository.save(Auction.builder()
                        .sellerId(seller.getId())
                        .productName("상품 이름")
                        .originPrice(1000L)
                        .currentPrice(1000L)
                        .originStock(10L)
                        .currentStock(10L)
                        .maximumPurchaseLimitCount(5)
                        .pricePolicy(new ConstantPricePolicy(100L))
                        .variationDuration(Duration.ofMinutes(10))
                        .startedAt(now)
                        .finishedAt(now.plusHours(1))
                        .build());

                int numThreads = 10;
                CountDownLatch doneSignal = new CountDownLatch(numThreads);
                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

                AtomicInteger successCount = new AtomicInteger();
                AtomicInteger failCount = new AtomicInteger();

                // when
                for (int i = 0; i < numThreads; i++) {
                    executorService.execute(() -> {
                        try {
                            auctioneer.process(
                                    new AuctionPurchaseRequestMessage(UUID.randomUUID(), buyer.getId(),
                                            auction.getId(), 1000L, 2L, now));
                            successCount.getAndIncrement();
                        } catch (Exception e) {
                            e.printStackTrace();
                            failCount.getAndIncrement();
                        } finally {
                            doneSignal.countDown();
                        }
                    });
                }

                doneSignal.await();
                executorService.shutdown();

                assertAll(
                        () -> assertThat(successCount.get()).isEqualTo(5L),
                        () -> assertThat(failCount.get()).isEqualTo(5L)
                );
            }
        }

        @Nested
        class 두명_중_한명이_포인트가_부족하다면 {

            @RepeatedTest(10)
            void 다른_한_명의_구매_과정이_원자적으로_처리되어야_한다() throws InterruptedException {
                // given
                Member seller = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
                Member buyer1 = memberRepository.save(Member.builder()
                        .signInId("buyerId1")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(999L))
                        .build());
                Member buyer2 = memberRepository.save(Member.builder()
                        .signInId("buyerId2")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(10000L))
                        .build());

                Auction auction = auctionRepository.save(Auction.builder()
                        .sellerId(seller.getId())
                        .productName("상품 이름")
                        .originPrice(1000L)
                        .currentPrice(1000L)
                        .originStock(10L)
                        .currentStock(10L)
                        .maximumPurchaseLimitCount(5)
                        .pricePolicy(new ConstantPricePolicy(100L))
                        .variationDuration(Duration.ofMinutes(10))
                        .startedAt(now)
                        .finishedAt(now.plusHours(1))
                        .build());

                int numThreads = 11;
                CountDownLatch doneSignal = new CountDownLatch(numThreads);
                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

                AtomicInteger successCount = new AtomicInteger();
                AtomicInteger failCount = new AtomicInteger();
                Random random = new Random();
                int randomNumber = random.nextInt(11);

                // when
                for (int i = 0; i < numThreads; i++) {
                    int currentIndex = i;
                    executorService.execute(() -> {
                        try {
                            if (currentIndex == randomNumber) {
                                auctioneer.process(
                                        new AuctionPurchaseRequestMessage(UUID.randomUUID(), buyer1.getId(),
                                                auction.getId(), 1000L, 1L, now));
                            } else {
                                auctioneer.process(
                                        new AuctionPurchaseRequestMessage(UUID.randomUUID(), buyer2.getId(),
                                                auction.getId(), 1000L, 1L, now));
                            }

                            successCount.getAndIncrement();
                        } catch (Exception e) {
                            e.printStackTrace();
                            failCount.getAndIncrement();
                        } finally {
                            doneSignal.countDown();
                        }
                    });
                }
                doneSignal.await();
                executorService.shutdown();

                // then
                long buyer1Point = memberRepository.findById(buyer1.getId()).get().getPoint().getAmount();
                long buyer2Point = memberRepository.findById(buyer2.getId()).get().getPoint().getAmount();
                long currentStock = auctionRepository.findById(auction.getId()).get().getCurrentStock();

                assertAll(
                        () -> assertThat(successCount.get()).isEqualTo(10L),
                        () -> assertThat(failCount.get()).isEqualTo(1L),
                        () -> assertThat(buyer1Point).isEqualTo(999L),
                        () -> assertThat(buyer2Point).isEqualTo(0L),
                        () -> assertThat(currentStock).isEqualTo(0L)
                );
            }
        }
    }

    @Nested
    class process_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 입찰이_진행된다() {
                // given
                Member seller = Member.builder()
                        .signInId("sellerId")
                        .password("password0")
                        .role(Role.SELLER)
                        .point(new Point(0))
                        .build();
                Member buyer = Member.builder()
                        .signInId("sellerId")
                        .password("password0")
                        .role(Role.BUYER)
                        .point(new Point(10000L))
                        .build();
                Member savedSeller = memberRepository.save(seller);
                Member savedBuyer = memberRepository.save(buyer);
                Auction runningAuction = Auction.builder()
                        .sellerId(savedSeller.getId())
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
                Auction savedAuction = auctionRepository.save(runningAuction);

                // when
                var message = new AuctionPurchaseRequestMessage(
                        UUID.randomUUID(),
                        savedBuyer.getId(),
                        savedAuction.getId(),
                        10000L,
                        1L,
                        now.minusMinutes(30)
                );
                auctioneer.process(message);

                // then
                Auction auction = auctionRepository.findById(savedAuction.getId()).get();
                Member finalBuyer = memberRepository.findById(savedBuyer.getId()).get();
                Member finalSeller = memberRepository.findById(savedSeller.getId()).get();
                assertAll(
                        () -> assertThat(auction.getCurrentStock()).isEqualTo(99L),
                        () -> assertThat(finalBuyer.getPoint()).isEqualTo(new Point(0)),
                        () -> assertThat(finalSeller.getPoint()).isEqualTo(new Point(10000L))
                );
            }
        }

        @Nested
        class 만약_요청한_구매자를_찾을_수_없다면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member seller = Member.builder()
                        .signInId("sellerId")
                        .password("password0")
                        .role(Role.SELLER)
                        .point(new Point(0))
                        .build();
                Member buyer = Member.builder()
                        .signInId("sellerId")
                        .password("password0")
                        .role(Role.BUYER)
                        .point(new Point(10000L))
                        .build();
                Member savedSeller = memberRepository.save(seller);
                Member savedBuyer = memberRepository.save(buyer);
                Auction runningAuction = Auction.builder()
                        .sellerId(savedSeller.getId())
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
                Auction savedAuction = auctionRepository.save(runningAuction);

                // expect
                assertThatThrownBy(
                        () -> {
                            var message = new AuctionPurchaseRequestMessage(
                                    UUID.randomUUID(),
                                    savedBuyer.getId() + 1L,
                                    savedAuction.getId(),
                                    10000L,
                                    1L,
                                    now.minusMinutes(30)
                            );
                            auctioneer.process(message);
                        })
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("사용자를 찾을 수 없습니다. id=" + (savedBuyer.getId() + 1L))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.M002);
            }
        }

        @Nested
        class 만약_판매자를_찾을_수_없다면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member seller = Member.builder()
                        .signInId("sellerId")
                        .password("password0")
                        .role(Role.SELLER)
                        .point(new Point(0))
                        .build();
                Member buyer = Member.builder()
                        .signInId("sellerId")
                        .password("password0")
                        .role(Role.BUYER)
                        .point(new Point(10000L))
                        .build();
                Member savedSeller = memberRepository.save(seller);
                Member savedBuyer = memberRepository.save(buyer);
                Auction runningAuction = Auction.builder()
                        .sellerId(savedSeller.getId() + 100)
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
                Auction savedAuction = auctionRepository.save(runningAuction);

                // expect
                assertThatThrownBy(
                        () -> {
                            var message = new AuctionPurchaseRequestMessage(
                                    UUID.randomUUID(),
                                    savedBuyer.getId(),
                                    savedAuction.getId(),
                                    10000L,
                                    1L,
                                    now.minusMinutes(30)
                            );
                            auctioneer.process(message);
                        })
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("사용자를 찾을 수 없습니다. id=" + runningAuction.getSellerId())
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.M002);
            }
        }

        @Nested
        class 만약_요청한_물건의_금액이_사용자가_요청한_금액과_다르다면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member seller = Member.builder()
                        .signInId("sellerId")
                        .password("password0")
                        .role(Role.SELLER)
                        .point(new Point(0))
                        .build();
                Member buyer = Member.builder()
                        .signInId("sellerId")
                        .password("password0")
                        .role(Role.BUYER)
                        .point(new Point(10000L))
                        .build();
                Member savedSeller = memberRepository.save(seller);
                Member savedBuyer = memberRepository.save(buyer);
                Auction runningAuction = Auction.builder()
                        .sellerId(savedSeller.getId())
                        .productName("productName")
                        .originPrice(10001L)
                        .currentPrice(10001L)
                        .originStock(100L)
                        .currentStock(100L)
                        .maximumPurchaseLimitCount(10L)
                        .pricePolicy(new ConstantPricePolicy(1000L))
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusMinutes(30))
                        .finishedAt(now.plusMinutes(30))
                        .isShowStock(true)
                        .build();
                Auction savedAuction = auctionRepository.save(runningAuction);

                // expect
                assertThatThrownBy(
                        () -> {
                            var message = new AuctionPurchaseRequestMessage(
                                    UUID.randomUUID(),
                                    savedBuyer.getId(),
                                    savedAuction.getId(),
                                    10000L,
                                    1L,
                                    now.minusMinutes(30)
                            );
                            auctioneer.process(message);
                        })
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(String.format("입력한 가격으로 상품을 구매할 수 없습니다. 현재가격: %d 입력가격: %d",
                                savedAuction.getCurrentPrice(), 10000L))
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A022);
            }
        }
    }


    @Nested
    class refund_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 환불이_진행된다() {
                // given
                Member buyer = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
                Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());

                Auction auction = Auction.builder()
                        .sellerId(1L)
                        .productName("productName")
                        .originPrice(10000L)
                        .currentPrice(10000L)
                        .originStock(100L)
                        .currentStock(99L)
                        .maximumPurchaseLimitCount(10L)
                        .pricePolicy(new ConstantPricePolicy(1000L))
                        .variationDuration(Duration.ofMinutes(10L))
                        .startedAt(now.minusHours(2))
                        .finishedAt(now.minusHours(1))
                        .isShowStock(true)
                        .build();
                auctionRepository.save(auction);

                Receipt receipt = Receipt.builder()
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .receiptStatus(ReceiptStatus.PURCHASED)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                Receipt savedReceipt = receiptRepository.save(receipt);

                // when
                var message = new AuctionRefundRequestMessage(new SignInInfo(buyer.getId(), Role.BUYER),
                        savedReceipt.getId(), now);
                auctioneer.refund(message);

                // then
                Receipt findedReceipt = receiptRepository.findById(savedReceipt.getId()).get();
                Member findedBuyer = memberRepository.findById(savedReceipt.getBuyerId()).get();
                Member findedSeller = memberRepository.findById(savedReceipt.getSellerId()).get();
                Auction findedAuction = auctionRepository.findById(savedReceipt.getAuctionId()).get();
                assertAll(
                        () -> assertThat(findedReceipt.getReceiptStatus()).isEqualTo(ReceiptStatus.REFUND),
                        () -> assertThat(findedBuyer.getPoint().getAmount()).isEqualTo(1100L),
                        () -> assertThat(findedSeller.getPoint().getAmount()).isEqualTo(900L),
                        () -> assertThat(findedAuction.getCurrentStock()).isEqualTo(100L)
                );
            }
        }

        @Nested
        class 만약_요청한_사용자가_구매자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member buyer = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
                Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                Receipt receipt = Receipt.builder()
                        .id(UUID.randomUUID())
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .receiptStatus(ReceiptStatus.PURCHASED)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                receiptRepository.save(receipt);

                // expect
                var message = new AuctionRefundRequestMessage(new SignInInfo(seller.getId(), Role.SELLER),
                        UUID.randomUUID(), now);
                assertThatThrownBy(() -> auctioneer.refund(message))
                        .isInstanceOf(AuthorizationException.class)
                        .hasMessage("구매자만 환불을 할 수 있습니다.")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.P000));
            }
        }

        @Nested
        class 만약_환불할_입찰_내역을_찾을_수_없다면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member buyer = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                // expect
                var message = new AuctionRefundRequestMessage(new SignInInfo(buyer.getId(), Role.BUYER),
                        UUID.randomUUID(), now);
                assertThatThrownBy(() -> auctioneer.refund(message))
                        .isInstanceOf(NotFoundException.class)
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.P002));
            }
        }

        @Nested
        class 만약_이미_환불된_입찰_내역이라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member buyer = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
                Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());

                Auction auction = AuctionFixture.createFinishedAuction();
                auctionRepository.save(auction);

                Receipt receipt = Receipt.builder()
                        .id(UUID.randomUUID())
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .receiptStatus(ReceiptStatus.REFUND)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                Receipt savedReceipt = receiptRepository.save(receipt);

                // expect
                var message = new AuctionRefundRequestMessage(new SignInInfo(buyer.getId(), Role.BUYER),
                        savedReceipt.getId(), now);
                assertThatThrownBy(() -> auctioneer.refund(message))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("이미 환불된 입찰 내역입니다.")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.R002));
            }
        }

        @Nested
        class 만약_입찰_내역의_구매자가_요청한_사용자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member buyer = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
                Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());

                Auction auction = AuctionFixture.createFinishedAuction();
                auctionRepository.save(auction);

                Receipt receipt = Receipt.builder()
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .receiptStatus(ReceiptStatus.PURCHASED)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                Receipt savedReceipt = receiptRepository.save(receipt);

                // expect
                Member unPurchasedBuyer = Member.builder()
                        .id(3L)
                        .signInId("unPurchasedBuyer")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(1000L))
                        .build();

                var message = new AuctionRefundRequestMessage(new SignInInfo(unPurchasedBuyer.getId(), Role.BUYER),
                        savedReceipt.getId(),
                        now);
                assertThatThrownBy(
                        () -> auctioneer.refund(message))
                        .isInstanceOf(AuthorizationException.class)
                        .hasMessage("환불할 입찰 내역의 구매자만 환불을 할 수 있습니다.")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.P004));
            }
        }

        @Nested
        class 만약_아직_종료되지_않은_경매상품을_환불하려_하면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member buyer = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
                Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                Receipt receipt = Receipt.builder()
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .receiptStatus(ReceiptStatus.PURCHASED)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                Receipt savedReceipt = receiptRepository.save(receipt);

                // expect
                var message = new AuctionRefundRequestMessage(new SignInInfo(buyer.getId(), Role.BUYER),
                        savedReceipt.getId(), now);
                assertThatThrownBy(
                        () -> auctioneer.refund(message))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("종료된 경매만 환불할 수 있습니다.")
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.P007);
            }
        }
    }
}

package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.context.ServiceTest;
import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.global.dto.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.global.dto.AuctionRefundRequestMessage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class BasicAuctioneerTest extends ServiceTest {

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

                LocalDateTime now = LocalDateTime.now();
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

                LocalDateTime now = LocalDateTime.now();
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

        @Nested
        class 다수가_동시에_같은_경매에_환불을_하는_경우 {

            @Test
            void 정상적으로_재고와_포인트가_수정되어야_한다() throws InterruptedException {
                // given
                Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());
                Member buyer = memberRepository.save(Member.builder()
                        .signInId("buyerId")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(1000000000L))
                        .build());

                LocalDateTime now = LocalDateTime.now();
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

                List<UUID> requestIds = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    UUID requestId = UUID.randomUUID();
                    requestIds.add(requestId);
                    auctioneer.process(new AuctionPurchaseRequestMessage(requestId, buyer.getId(), auction.getId(), 1000L, 1L, now));
                }

                SignInInfo buyerInfo = new SignInInfo(buyer.getId(), Role.BUYER);

                int numThreads = 10;
                CountDownLatch doneSignal = new CountDownLatch(numThreads);
                ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

                // when
                for (int i = 0; i < numThreads; i++) {
                    UUID finalI = requestIds.get(i);
                    executorService.execute(() -> {
                        try {
                            auctioneer.refund(new AuctionRefundRequestMessage(buyerInfo, finalI, now.plusHours(2)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            doneSignal.countDown();
                        }
                    });
                }
                doneSignal.await();
                executorService.shutdown();

                // then
                Auction updatedAuction = auctionRepository.findById(auction.getId()).get();
                Member updatedSeller = memberRepository.findById(seller.getId()).get();
                Member updatedBuyer = memberRepository.findById(buyer.getId()).get();
                assertAll(
                        () -> assertThat(updatedAuction.getCurrentStock()).isEqualTo(10L),
                        () -> assertThat(updatedSeller.getPoint().getAmount()).isEqualTo(1000L),
                        () -> assertThat(updatedBuyer.getPoint().getAmount()).isEqualTo(1000000000L)
                );
            }
        }
    }
}

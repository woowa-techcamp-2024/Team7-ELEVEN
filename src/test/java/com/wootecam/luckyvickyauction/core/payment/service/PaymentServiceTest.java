package com.wootecam.luckyvickyauction.core.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.context.ServiceTest;
import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.fixture.AuctionFixture;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.core.payment.domain.Receipt;
import com.wootecam.luckyvickyauction.core.payment.domain.ReceiptStatus;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.AuthorizationException;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentServiceTest extends ServiceTest {

    @Nested
    class process_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 입찰이_진행된다() {
                // given
                LocalDateTime now = LocalDateTime.now();
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
                auctioneer.process(new SignInInfo(savedBuyer.getId(), Role.BUYER), 10000L, savedAuction.getId(), 1L,
                        now.minusMinutes(30));

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
                LocalDateTime now = LocalDateTime.now();
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
                        () -> auctioneer.process(new SignInInfo(savedBuyer.getId() + 1L, Role.BUYER), 10000L,
                                savedAuction.getId(), 1L, now.minusMinutes(30)))
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
                LocalDateTime now = LocalDateTime.now();
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
                        () -> auctioneer.process(new SignInInfo(savedBuyer.getId(), Role.BUYER), 10000L,
                                savedAuction.getId(), 1L, now.minusMinutes(30)))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("사용자를 찾을 수 없습니다. id=" + runningAuction.getSellerId())
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.M002);
            }
        }

        @Nested
        class 만약_요청한_물건의_금액이_사용자가_가진_포인트보다_크다면 {

            @Test
            void 예외가_발생한다() {
                // given
                LocalDateTime now = LocalDateTime.now();
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
                        () -> auctioneer.process(new SignInInfo(savedBuyer.getId(), Role.BUYER), 10000L,
                                savedAuction.getId(), 1L, now.minusMinutes(30)))
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

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                LocalDateTime now = LocalDateTime.now();
                Receipt receipt = Receipt.builder()
                        .id(1L)
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

                // when
                paymentService.refund(new SignInInfo(buyer.getId(), Role.BUYER), 1L);

                // then
                Receipt savedReceipt = receiptRepository.findById(1L).get();
                Member savedBuyer = memberRepository.findById(savedReceipt.getBuyerId()).get();
                Member savedSeller = memberRepository.findById(savedReceipt.getSellerId()).get();
                Auction savedAuction = auctionRepository.findById(savedReceipt.getAuctionId()).get();
                assertAll(
                        () -> assertThat(savedReceipt.getReceiptStatus()).isEqualTo(ReceiptStatus.REFUND),
                        () -> assertThat(savedBuyer.getPoint().getAmount()).isEqualTo(1100L),
                        () -> assertThat(savedSeller.getPoint().getAmount()).isEqualTo(900L),
                        () -> assertThat(savedAuction.getCurrentStock()).isEqualTo(1L)
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

                LocalDateTime now = LocalDateTime.now();
                Receipt receipt = Receipt.builder()
                        .id(1L)
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
                assertThatThrownBy(() -> paymentService.refund(new SignInInfo(seller.getId(), Role.SELLER), 1L))
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
                assertThatThrownBy(() -> paymentService.refund(new SignInInfo(buyer.getId(), Role.BUYER), 1L))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage("환불할 입찰 내역을 찾을 수 없습니다. 내역 id=1")
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

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                LocalDateTime now = LocalDateTime.now();
                Receipt receipt = Receipt.builder()
                        .id(1L)
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
                receiptRepository.save(receipt);

                // expect
                assertThatThrownBy(() -> paymentService.refund(new SignInInfo(buyer.getId(), Role.BUYER), 1L))
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

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                LocalDateTime now = LocalDateTime.now();
                Receipt receipt = Receipt.builder()
                        .id(1L)
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
                Member unPurchasedBuyer = Member.builder()
                        .id(3L)
                        .signInId("unPurchasedBuyer")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(1000L))
                        .build();

                assertThatThrownBy(
                        () -> paymentService.refund(new SignInInfo(unPurchasedBuyer.getId(), Role.BUYER), 1L))
                        .isInstanceOf(AuthorizationException.class)
                        .hasMessage("환불할 입찰 내역의 구매자만 환불을 할 수 있습니다.")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.P004));
            }
        }
    }

    @Nested
    class chargePoint_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 포인트가_충전된다() {
                // given
                Member member = Member.builder()
                        .signInId("testSignInId")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(0))
                        .build();
                Member savedMember = memberRepository.save(member);

                // when
                paymentService.chargePoint(new SignInInfo(savedMember.getId(), Role.BUYER), 1000L);

                // then
                Member resultMember = memberRepository.findById(savedMember.getId()).get();
                Point point = resultMember.getPoint();
                assertThat(point.getAmount()).isEqualTo(1000L);
            }

        }

        @Nested
        class 만약_충전할_포인트가_음수라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member member = Member.createMemberWithRole("testSignInId", "password00", "BUYER");
                Member savedMember = memberRepository.save(member);

                // expect
                assertThatThrownBy(
                        () -> paymentService.chargePoint(new SignInInfo(savedMember.getId(), Role.BUYER), -1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("포인트는 음수가 될 수 없습니다. 충전 포인트=-1")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.P005));
            }


        }
    }
}

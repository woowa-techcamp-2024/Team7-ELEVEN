package com.wootecam.luckyvickyauction.core.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.fixture.AuctionFixture;
import com.wootecam.luckyvickyauction.core.auction.infra.FakeAuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.core.member.infra.FakeMemberRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.core.payment.infra.FakeBidHistoryRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// TODO: AuctionService 세부 사항 결정되면 테스트
class PaymentServiceTest {

    private AuctionRepository auctionRepository;
    private AuctionService auctionService;
    private MemberRepository memberRepository;
    private BidHistoryRepository bidHistoryRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        auctionRepository = new FakeAuctionRepository();
        auctionService = new AuctionService(auctionRepository);
        memberRepository = new FakeMemberRepository();
        bidHistoryRepository = new FakeBidHistoryRepository();
        paymentService = new PaymentService(auctionService, memberRepository, bidHistoryRepository);
    }

    @Nested
    class process_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 입찰이_진행된다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_요청한_사용자가_구매자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_판매자를_찾을_수_없다면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_요청한_물건의_금액이_사용자가_가진_포인트보다_많다면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
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
                Member buyer = MemberFixture.createBuyerWithDefaultPoint();
                memberRepository.save(buyer);

                Member seller = MemberFixture.createSellerWithDefaultPoint();
                memberRepository.save(seller);

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                ZonedDateTime now = ZonedDateTime.now();
                BidHistory bidHistory = BidHistory.builder()
                        .id(1L)
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .bidStatus(BidStatus.BID)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                bidHistoryRepository.save(bidHistory);

                // when
                paymentService.refund(new SignInInfo(buyer.getId(), Role.BUYER), 1L);

                // then
                BidHistory savedBidHistory = bidHistoryRepository.findById(1L).get();
                Member savedBuyer = memberRepository.findById(savedBidHistory.getBuyerId()).get();
                Member savedSeller = memberRepository.findById(savedBidHistory.getSellerId()).get();
                Auction savedAuction = auctionRepository.findById(savedBidHistory.getAuctionId()).get();
                assertAll(
                        () -> assertThat(savedBidHistory.getBidStatus()).isEqualTo(BidStatus.REFUND),
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
                Member buyer = MemberFixture.createBuyerWithDefaultPoint();
                memberRepository.save(buyer);

                Member seller = MemberFixture.createSellerWithDefaultPoint();
                memberRepository.save(seller);

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                ZonedDateTime now = ZonedDateTime.now();
                BidHistory bidHistory = BidHistory.builder()
                        .id(1L)
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .bidStatus(BidStatus.BID)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                bidHistoryRepository.save(bidHistory);

                // expect
                assertThatThrownBy(() -> paymentService.refund(new SignInInfo(seller.getId(), Role.SELLER), 1L))
                        .isInstanceOf(UnauthorizedException.class)
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
                Member buyer = MemberFixture.createBuyerWithDefaultPoint();
                memberRepository.save(buyer);

                Member seller = MemberFixture.createSellerWithDefaultPoint();
                memberRepository.save(seller);

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
                Member buyer = MemberFixture.createBuyerWithDefaultPoint();
                memberRepository.save(buyer);

                Member seller = MemberFixture.createSellerWithDefaultPoint();
                memberRepository.save(seller);

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                ZonedDateTime now = ZonedDateTime.now();
                BidHistory bidHistory = BidHistory.builder()
                        .id(1L)
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .bidStatus(BidStatus.REFUND)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                bidHistoryRepository.save(bidHistory);

                // expect
                assertThatThrownBy(() -> paymentService.refund(new SignInInfo(buyer.getId(), Role.BUYER), 1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("이미 환불된 입찰 내역입니다.")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.B005));
            }
        }

        @Nested
        class 만약_입찰_내역의_구매자가_요청한_사용자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member buyer = MemberFixture.createBuyerWithDefaultPoint();
                memberRepository.save(buyer);

                Member seller = MemberFixture.createSellerWithDefaultPoint();
                memberRepository.save(seller);

                Auction auction = AuctionFixture.createSoldOutAuction();
                auctionRepository.save(auction);

                ZonedDateTime now = ZonedDateTime.now();
                BidHistory bidHistory = BidHistory.builder()
                        .id(1L)
                        .auctionId(1L)
                        .productName("test")
                        .price(100L)
                        .quantity(1L)
                        .bidStatus(BidStatus.BID)
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                bidHistoryRepository.save(bidHistory);

                // expect
                Member unbidBuyer = Member.builder()
                        .id(3L)
                        .signInId("unbidBuyer")
                        .password("password00")
                        .role(Role.BUYER)
                        .point(new Point(1000L))
                        .build();

                assertThatThrownBy(() -> paymentService.refund(new SignInInfo(unbidBuyer.getId(), Role.BUYER), 1L))
                        .isInstanceOf(UnauthorizedException.class)
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
                Member member = new Member(1L, "testSignInId", "password00", Role.BUYER, new Point(0));

                // when
                paymentService.chargePoint(member, 1000L);

                // then
                Member savedMember = memberRepository.findById(1L).get();
                Point savedMemberPoint = savedMember.getPoint();
                assertThat(savedMemberPoint.getAmount()).isEqualTo(1000L);
            }

        }

        @Nested
        class 만약_충전할_포인트가_음수라면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member member = Member.createMemberWithRole("testSignInId", "password00", "BUYER");

                // expect
                assertThatThrownBy(() -> paymentService.chargePoint(member, -1L))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("포인트는 음수가 될 수 없습니다. 충전 포인트=-1")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.P005));
            }


        }
    }
}

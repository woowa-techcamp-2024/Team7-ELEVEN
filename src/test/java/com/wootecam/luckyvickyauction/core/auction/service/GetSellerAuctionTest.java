package com.wootecam.luckyvickyauction.core.auction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.fixture.AuctionFixture;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.infra.FakeAuctionRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/138">#138</a>
 */
abstract class GetSellerAuctionTest {

    private AuctionRepository auctionRepository;
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionRepository = new FakeAuctionRepository();
        auctionService = new AuctionService(auctionRepository);
    }

    @Nested
    class 정상적인_요청이_오면 {

        @Test
        void 판매자의_경매목록을_반환한다() {
            // given
            Member seller = MemberFixture.createSellerWithDefaultPoint();
            Auction auction = auctionRepository.save(AuctionFixture.createWaitingAuction());

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
            Member seller = MemberFixture.createSellerWithDefaultPoint();
            Auction auction = auctionRepository.save(AuctionFixture.createWaitingAuction());
            SignInInfo signInInfo = new SignInInfo(seller.getId() + 1, seller.getRole());

            // expect
            assertThatThrownBy(() -> auctionService.getSellerAuction(signInInfo, auction.getId()))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("판매자는 자신이 등록한 경매만 조회할 수 있습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.A027);
        }
    }
}

package com.wootecam.luckyvickyauction.core.auction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.dto.CancelAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.fixture.AuctionFixture;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.infra.FakeAuctionRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

// AuctionService.cancelAuction: 경매 취소 테스트
abstract class CancelAuctionTest {

    private AuctionRepository auctionRepository;
    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionRepository = new FakeAuctionRepository();
        auctionService = new AuctionService(auctionRepository);
    }

    @Test
    void 정상적으로_취소되어_경매가_삭제된다() {
        // given
        SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
        CancelAuctionCommand command = new CancelAuctionCommand(ZonedDateTime.now(), 1L);
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
        CancelAuctionCommand command = new CancelAuctionCommand(ZonedDateTime.now(), 1L);

        // expect
        assertThatThrownBy(
                () -> auctionService.cancelAuction(signInInfo, command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("판매자만 경매를 취소할 수 있습니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.A024));
    }

    @Test
    void 경매를_등록한_판매자와_경매를_수정하려는_판매자가_다른_경우_예외가_발생한다() {
        // given
        SignInInfo signInInfo = new SignInInfo(2L, Role.SELLER);
        CancelAuctionCommand command = new CancelAuctionCommand(ZonedDateTime.now(), 1L);
        Auction auction = AuctionFixture.createWaitingAuction();
        auctionRepository.save(auction);

        // expect
        assertThatThrownBy(
                () -> auctionService.cancelAuction(signInInfo, command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("자신이 등록한 경매만 취소할 수 있습니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.A025));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("generateInvalidAuction")
    void 경매_상태가_시작전이_아닌경우_예외가_발생한다(String displayName, Auction auction) {
        // given
        SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
        CancelAuctionCommand command = new CancelAuctionCommand(ZonedDateTime.now(), 1L);

        auctionRepository.save(auction);

        // expect
        assertThatThrownBy(
                () -> auctionService.cancelAuction(signInInfo, command))
                .isInstanceOf(BadRequestException.class)
                .hasMessageStartingWith("시작 전인 경매만 취소할 수 있습니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.A026));
    }
}

package com.wootecam.luckyvickyauction.core.auction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;

// AuctionService.cancelAuction: 경매 취소 테스트
class CancelAuctionTest {

    protected AuctionRepository auctionRepository;
    protected AuctionService auctionService;

    @Test
    void 판매자가_권한이_없는_사용자_접근시_예외가_발생한다() {
        // given
        SignInInfo signInInfo = new SignInInfo(1L, Role.BUYER);

        // expect
        assertThatThrownBy(() -> auctionService.cancelAuction(signInInfo, 1L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("판매자만 경매를 취소할 수 있습니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                        ErrorCode.A024));
    }

}

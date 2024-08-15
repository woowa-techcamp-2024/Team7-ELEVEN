package com.wootecam.luckyvickyauction.core.payment.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.global.exception.BusinessException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BidHistoryInfoTest {

    static Stream<Arguments> bidHistoryInfoArguments() {
        return Stream.of(
                Arguments.of("상품 이름은 비어있을 수 없습니다.", ErrorCode.B002,
                        1L, "", 10000, 1, BidStatus.BID, 1L,
                        new Member(1L, "seller", "password", Role.SELLER, null),
                        new Member(2L, "buyer", "password", Role.BUYER, null),
                        ZonedDateTime.now()),
                Arguments.of("상품 이름은 빈 칸일 수 없습니다.", ErrorCode.B002,
                        1L, "   ", 10000, 1, BidStatus.BID, 1L,
                        new Member(1L, "seller", "password", Role.SELLER, null),
                        new Member(2L, "buyer", "password", Role.BUYER, null),
                        ZonedDateTime.now()),
                Arguments.of("입찰 가격은 0보다 커야 합니다. 입찰 가격: 0", ErrorCode.B003,
                        1L, "상품이름", 0, 1, BidStatus.BID, 1L,
                        new Member(1L, "seller", "password", Role.SELLER, null),
                        new Member(2L, "buyer", "password", Role.BUYER, null),
                        ZonedDateTime.now()),
                Arguments.of("수량은 0보다 커야 합니다. 수량: 0", ErrorCode.B004,
                        1L, "상품이름", 10000, 0, BidStatus.BID, 1L,
                        new Member(1L, "seller", "password", Role.SELLER, null),
                        new Member(2L, "buyer", "password", Role.BUYER, null),
                        ZonedDateTime.now()),
                Arguments.of("상품 이름은 Null일 수 없습니다.", ErrorCode.G000,
                        1L, null, 10000, 1, BidStatus.REFUND, 1L,
                        new Member(1L, "seller", "password", Role.SELLER, null),
                        new Member(2L, "buyer", "password", Role.BUYER, null),
                        ZonedDateTime.now()),
                Arguments.of("입찰 상태는 Null일 수 없습니다.", ErrorCode.G000,
                        1L, "상품이름", 10000, 1, null, 1L,
                        new Member(1L, "seller", "password", Role.SELLER, null),
                        new Member(2L, "buyer", "password", Role.BUYER, null),
                        ZonedDateTime.now()),
                Arguments.of("판매자 정보는 Null일 수 없습니다.", ErrorCode.G000,
                        1L, "상품이름", 10000, 1, BidStatus.BID, 1L,
                        null,
                        new Member(2L, "buyer", "password", Role.BUYER, null),
                        ZonedDateTime.now()),
                Arguments.of("구매자 정보는 Null일 수 없습니다.", ErrorCode.G000,
                        1L, "상품이름", 10000, 1, BidStatus.BID, 1L,
                        new Member(1L, "seller", "password", Role.SELLER, null),
                        null,
                        ZonedDateTime.now()),
                Arguments.of("거래 이력은 Null일 수 없습니다.", ErrorCode.G000,
                        1L, "상품이름", 10000, 1, BidStatus.BID, 1L,
                        new Member(1L, "seller", "password", Role.SELLER, null),
                        new Member(2L, "buyer", "password", Role.BUYER, null),
                        null)
        );
    }

    @Test
    void 입찰_내역_생성_요청을_정상적으로_처리한다() {
        // given
        Long id = 1L;
        String productName = "상품이름";
        long price = 10000L;
        long quantity = 1L;
        BidStatus bidStatus = BidStatus.BID;
        Long auctionId = 1L;
        Member seller = new Member(1L, "seller", "password", Role.SELLER, null);
        Member buyer = new Member(2L, "buyer", "password", Role.BUYER, null);
        ZonedDateTime createdAt = ZonedDateTime.now();

        // when
        BidHistoryInfo bidHistoryInfo = new BidHistoryInfo(id, productName, price, quantity, bidStatus, auctionId,
                seller, buyer, createdAt);

        // then
        assertAll(
                () -> assertThat(bidHistoryInfo.bidHistoryId()).isEqualTo(id),
                () -> assertThat(bidHistoryInfo.productName()).isEqualTo(productName),
                () -> assertThat(bidHistoryInfo.price()).isEqualTo(price),
                () -> assertThat(bidHistoryInfo.quantity()).isEqualTo(quantity),
                () -> assertThat(bidHistoryInfo.bidStatus()).isEqualTo(bidStatus),
                () -> assertThat(bidHistoryInfo.auctionId()).isEqualTo(auctionId),
                () -> assertThat(bidHistoryInfo.seller()).isEqualTo(seller),
                () -> assertThat(bidHistoryInfo.buyer()).isEqualTo(buyer)
        );
    }

    @ParameterizedTest(name = "예외: {0}")
    @MethodSource("bidHistoryInfoArguments")
    void 입찰_내역_생성_요청이_잘못된_경우_예외가_발생한다(
            String expectedMessage,
            ErrorCode errorCode,
            Long id,
            String productName,
            long price,
            long quantity,
            BidStatus bidStatus,
            Long auctionId,
            Member seller,
            Member buyer,
            ZonedDateTime createdAt
    ) {
        // expect
        assertThatThrownBy(
                () -> new BidHistoryInfo(id, productName, price, quantity, bidStatus, auctionId, seller, buyer,
                        createdAt))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", errorCode));
    }

}
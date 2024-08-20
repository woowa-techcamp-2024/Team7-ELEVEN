package com.wootecam.luckyvickyauction.core.payment.dto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class BidHistoryInfoTest {

    @Test
    void 입찰_내역_생성_요청을_정상적으로_처리한다() {
        // given
        Long id = 1L;
        String productName = "상품이름";
        long price = 10000L;
        long quantity = 1L;
        BidStatus bidStatus = BidStatus.PURCHASED;
        Long auctionId = 1L;
        Member seller = Member.builder()
                .id(1L)
                .signInId("buyerId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(1000L))
                .build();
        Member buyer = Member.builder()
                .id(2L)
                .signInId("sellerId")
                .password("password00")
                .role(Role.SELLER)
                .point(new Point(1000L))
                .build();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        BidHistoryInfo bidHistoryInfo = new BidHistoryInfo(id, productName, price, quantity, bidStatus, auctionId,
                seller.getId(), buyer.getId(), createdAt, updatedAt);

        // then
        assertAll(
                () -> assertThat(bidHistoryInfo.bidHistoryId()).isEqualTo(id),
                () -> assertThat(bidHistoryInfo.productName()).isEqualTo(productName),
                () -> assertThat(bidHistoryInfo.price()).isEqualTo(price),
                () -> assertThat(bidHistoryInfo.quantity()).isEqualTo(quantity),
                () -> assertThat(bidHistoryInfo.bidStatus()).isEqualTo(bidStatus),
                () -> assertThat(bidHistoryInfo.auctionId()).isEqualTo(auctionId),
                () -> assertThat(bidHistoryInfo.sellerId()).isEqualTo(seller.getId()),
                () -> assertThat(bidHistoryInfo.buyerId()).isEqualTo(buyer.getId())
        );
    }
}

package com.wootecam.luckyvickyauction.core.payment.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class BidHistoryInfoTest {

    @Test
    void 입찰_내역_생성_요청을_정상적으로_처리한다() {
        // given
        Long id = 1L;
        String productName = "상품이름";
        long price = 10000L;
        long quantity = 1L;
        BidStatus bidStatus = BidStatus.BID;
        Long auctionId = 1L;
        Member seller = MemberFixture.createSellerWithDefaultPoint();
        Member buyer = MemberFixture.createBuyerWithDefaultPoint();
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();

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

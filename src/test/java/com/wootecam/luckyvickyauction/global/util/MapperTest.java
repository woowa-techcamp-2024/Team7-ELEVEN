package com.wootecam.luckyvickyauction.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import org.junit.jupiter.api.Test;

class MapperTest {

    @Test
    public void 거래내역_entity를_BuyerReceiptSimpleInfo으로_변환하면_도메인의_정보가_동일하게_전달된다() {
        // given
        Member buyer = Member.builder()
                .id(1L)
                .signInId("buyer")
                .build();

        Member seller = Member.builder()
                .id(2L)
                .signInId("seller")
                .build();

        BidHistory history = BidHistory.builder()
                .id(1L)
                .auctionId(232L)
                .productName("상품 이름")
                .price(1000L)
                .quantity(1L)
                .buyer(buyer)
                .seller(seller)
                .build();

        // when
        BuyerReceiptSimpleInfo dto = Mapper.convertToBuyerReceiptSimpleInfo(history);

        // then
        assertAll(
                () -> assertEquals(history.getId(), dto.id()),
                () -> assertThat(history.getBidStatus()).isEqualTo(dto.type()),
                () -> assertEquals(history.getAuctionId(), dto.auctionId()),
                () -> assertEquals(history.getQuantity(), dto.quantity()),
                () -> assertEquals(history.getPrice(), dto.price())
        );
    }

    @Test
    public void 경매_entity를_BuyerAuctionSimpleInfo로_변환하면_도메인의_정보가_동일하게_전달된다() {
        // given
        Auction auction = Auction.builder()
                .id(1L)
                .sellerId(1L)
                .productName("상품 이름")
                .originPrice(1000L)
                .currentPrice(1000L)
                .currentStock(10L)
                .maximumPurchaseLimitCount(1L)
                .isShowStock(true)
                .pricePolicy(PricePolicy.createConstantPricePolicy(100))
                .build();

        // when
        BuyerAuctionSimpleInfo dto = Mapper.convertToBuyerAuctionSimpleInfo(auction);

        // then
        assertAll(
                () -> assertEquals(auction.getId(), dto.id()),
                () -> assertEquals(auction.getProductName(), dto.title()),
                () -> assertEquals(auction.getCurrentPrice(), dto.price()),
                () -> assertEquals(auction.getStartedAt(), dto.startedAt()),
                () -> assertEquals(auction.getFinishedAt(), dto.finishedAt())
        );
    }
}
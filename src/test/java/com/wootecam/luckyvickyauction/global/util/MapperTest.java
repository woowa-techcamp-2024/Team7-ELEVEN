package com.wootecam.luckyvickyauction.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MapperTest {

    @Test
    public void 거래내역_entity를_BuyerReceiptSimpleInfo으로_변환하면_도메인의_정보가_동일하게_전달된다() {
        // given
        Member buyer = Member.builder()
                .id(1L)
                .signInId("buyerId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(1000L))
                .build();
        Member seller = Member.builder()
                .id(2L)
                .signInId("sellerId")
                .password("password00")
                .role(Role.SELLER)
                .point(new Point(1000L))
                .build();

        BidHistory history = BidHistory.builder()
                .id(1L)
                .auctionId(232L)
                .productName("상품 이름")
                .price(1000L)
                .quantity(1L)
                .sellerId(seller.getId())
                .buyerId(buyer.getId())
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
        LocalDateTime now = LocalDateTime.now();
        Auction auction = Auction.builder()
                .id(1L)
                .sellerId(1L)
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

    @Test
    public void 경매_entity를_SellerAuctionSimpleInfo로_변환하면_도메인의_정보가_동일하게_전달된다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Auction auction = Auction.builder()
                .id(1L)
                .sellerId(1L)
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
        // when
        SellerAuctionSimpleInfo dto = Mapper.convertToSellerAuctionSimpleInfo(auction);

        // then
        assertAll(
                () -> assertEquals(auction.getId(), dto.id()),
                () -> assertEquals(auction.getProductName(), dto.title()),
                () -> assertEquals(auction.getOriginPrice(), dto.originPrice()),
                () -> assertEquals(auction.getCurrentPrice(), dto.currentPrice()),
                () -> assertEquals(auction.getOriginStock(), dto.totalStock()),
                () -> assertEquals(auction.getCurrentStock(), dto.currentStock()),
                () -> assertEquals(auction.getStartedAt(), dto.startedAt()),
                () -> assertEquals(auction.getFinishedAt(), dto.finishedAt())
        );
    }

    @Nested
    class 회원정보_맵핑시에 extends MapperMemberTest {
    }

    @Nested
    class 경매_맵핑시에 extends MapperAuctionTest {
    }


    @Nested
    class 거래내역_맵핑시에 extends MapperReceiptTest {
    }
}

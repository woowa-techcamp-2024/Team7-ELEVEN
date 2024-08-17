package com.wootecam.luckyvickyauction.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MapperTest {

    @Test
    public void 거래내역_entity를_BuyerReceiptSimpleInfo으로_변환하면_도메인의_정보가_동일하게_전달된다() {
        // given
        Member buyer = MemberFixture.createBuyerWithDefaultPoint();
        Member seller = MemberFixture.createSellerWithDefaultPoint();

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
        ZonedDateTime now = ZonedDateTime.now();
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
        ZonedDateTime now = ZonedDateTime.now();
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
    class 영속성_엔티티_변환_케이스 {

        @Test
        public void 경매_영속성_엔티티를_도메인_엔티티로_변환하면_정보가_동일하다() {
            // given
            ZonedDateTime now = ZonedDateTime.now();
            AuctionEntity entity = AuctionEntity.builder()
                    .id(1L)
                    .sellerId(2L)
                    .productName("상품 이름")
                    .originPrice(1000L)
                    .currentPrice(1000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();

            // when
            Auction auction = Mapper.convertToAuction(entity);

            // then
            assertAll(
                    () -> assertEquals(entity.getId(), auction.getId()),
                    () -> assertEquals(entity.getSellerId(), auction.getSellerId()),
                    () -> assertEquals(entity.getProductName(), auction.getProductName()),
                    () -> assertEquals(entity.getOriginPrice(), auction.getOriginPrice()),
                    () -> assertEquals(entity.getCurrentPrice(), auction.getCurrentPrice()),
                    () -> assertEquals(entity.getOriginStock(), auction.getOriginStock()),
                    () -> assertEquals(entity.getCurrentStock(), auction.getCurrentStock()),
                    () -> assertEquals(entity.getMaximumPurchaseLimitCount(), auction.getMaximumPurchaseLimitCount()),
                    () -> assertEquals(entity.getPricePolicy(), auction.getPricePolicy()),
                    () -> assertEquals(entity.getVariationDuration(), auction.getVariationDuration()),
                    () -> assertEquals(entity.getStartedAt(), auction.getStartedAt()),
                    () -> assertEquals(entity.getFinishedAt(), auction.getFinishedAt()),
                    () -> assertEquals(entity.isShowStock(), auction.isShowStock())
            );
        }

        @Test
        public void 거래내역_영속성_엔티티를_도메인_엔티티로_변환하면_정보가_동일하다() {
            // given
            ReceiptEntity entity = ReceiptEntity.builder()
                    .id(1L)
                    .auctionId(2L)
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .sellerId(3L)
                    .buyerId(4L)
                    .bidStatus(BidStatus.BID)
                    .updatedAt(ZonedDateTime.now())
                    .createdAt(ZonedDateTime.now().plusHours(1))
                    .build();

            // when
            BidHistory domainEntity = Mapper.convertToReceipt(entity);

            // then
            assertAll(
                    () -> assertEquals(entity.getId(), domainEntity.getId()),
                    () -> assertEquals(entity.getAuctionId(), domainEntity.getAuctionId()),
                    () -> assertEquals(entity.getProductName(), domainEntity.getProductName()),
                    () -> assertEquals(entity.getPrice(), domainEntity.getPrice()),
                    () -> assertEquals(entity.getQuantity(), domainEntity.getQuantity()),
                    () -> assertEquals(entity.getSellerId(), domainEntity.getSellerId()),
                    () -> assertEquals(entity.getBuyerId(), domainEntity.getBuyerId()),
                    () -> assertEquals(entity.getBidStatus(), domainEntity.getBidStatus()),
                    () -> assertEquals(entity.getCreatedAt(), domainEntity.getCreatedAt()),
                    () -> assertEquals(entity.getUpdatedAt(), domainEntity.getUpdatedAt())
            );
        }
    }
}

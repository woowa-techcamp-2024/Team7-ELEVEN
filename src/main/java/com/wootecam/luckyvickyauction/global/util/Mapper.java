package com.wootecam.luckyvickyauction.global.util;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Mapper {

    public static AuctionInfo convertToAuctionInfo(Auction auction) {
        return AuctionInfo.builder()
                .auctionId(auction.getId())
                .sellerId(auction.getSellerId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .stock(auction.getStock())
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .isShowStock(auction.isShowStock())
                .status(auction.getStatus())
                .build();
    }

    /**
     * Auction을 BuyerAuctionInfo로 변환 - stock 노출 여부를 확인하여 노출 여부에 따라 stock을 노출하거나 노출하지 않습니다
     *
     * @param auction
     * @return
     */
    public static BuyerAuctionInfo convertToBuyerAuctionInfo(Auction auction) {
        long stock = auction.isShowStock() ? auction.getStock() : 0;

        return BuyerAuctionInfo.builder()
                .auctionId(auction.getId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .stock(stock)
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .isShowStock(auction.isShowStock())
                .status(auction.getStatus())
                .build();
    }

    /**
     * Auction을 SellerAuctionInfo로 변환 - 현재 판매량 등의 필드를 추가해야 합니다
     *
     * @param auction
     * @return
     */
    public static SellerAuctionInfo convertToSellerAuctionInfo(Auction auction) {
        return SellerAuctionInfo.builder()
                .auctionId(auction.getId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .stock(auction.getStock())
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .isShowStock(auction.isShowStock())
                .status(auction.getStatus())
                .build();
    }
}

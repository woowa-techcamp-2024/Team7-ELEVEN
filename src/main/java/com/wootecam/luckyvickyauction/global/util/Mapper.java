package com.wootecam.luckyvickyauction.global.util;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
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
}

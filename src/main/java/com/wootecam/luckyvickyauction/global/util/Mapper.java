package com.wootecam.luckyvickyauction.global.util;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 현재 재고는 currentStock을 담아 놓았습니다
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Mapper {

    /**
     * Auction을 AuctionInfo로 변환 - 재고의 경우에는 currentStock을 담아 놓았습니다
     *
     * @param auction
     * @return
     */
    public static AuctionInfo convertToAuctionInfo(Auction auction) {
        return AuctionInfo.builder()
                .auctionId(auction.getId())
                .sellerId(auction.getSellerId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .stock(auction.getCurrentStock())
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .isShowStock(auction.isShowStock())
                .build();
    }

    public static BidHistoryInfo convertToBidHistoryInfo(BidHistory bidHistory) {
        return BidHistoryInfo.builder()
                .bidHistoryId(bidHistory.getId())
                .productName(bidHistory.getProductName())
                .price(bidHistory.getPrice())
                .quantity(bidHistory.getQuantity())
                .bidStatus(bidHistory.getBidStatus())
                .auctionId(bidHistory.getAuctionId())
                .sellerId(bidHistory.getSellerId())
                .buyerId(bidHistory.getBuyerId())
                .createdAt(bidHistory.getCreatedAt())
                .updatedAt(bidHistory.getUpdatedAt())
                .build();
    }

    /**
     * Auction을 BuyerAuctionInfo로 변환 - stock 노출 여부를 확인하여 노출 여부에 따라 stock을 노출하거나 노출하지 않습니다 - 만약 isShowStock이 false라면
     * stock을 0으로 지정해 구매자에게 보이지 않게 합니다
     *
     * @param auction
     * @return
     */
    public static BuyerAuctionInfo convertToBuyerAuctionInfo(Auction auction) {
        long stock = auction.isShowStock() ? auction.getCurrentStock() : 0;

        return BuyerAuctionInfo.builder()
                .auctionId(auction.getId())
                .sellerId(auction.getSellerId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .stock(stock)
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .pricePolicy(auction.getPricePolicy())
                .variationDuration(auction.getVariationDuration())
                .startedAt(auction.getStartedAt())
                .finishedAt(auction.getFinishedAt())
                .isShowStock(auction.isShowStock())
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
                .originStock(auction.getOriginStock())
                .currentStock(auction.getCurrentStock())
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .pricePolicy(auction.getPricePolicy())
                .variationDuration(auction.getVariationDuration())
                .startedAt(auction.getStartedAt())
                .finishedAt(auction.getFinishedAt())
                .isShowStock(auction.isShowStock())
                .build();
    }

    public static BuyerReceiptSimpleInfo convertToBuyerReceiptSimpleInfo(BidHistory history) {
        return new BuyerReceiptSimpleInfo(
                history.getId(),
                history.getBidStatus(),
                history.getAuctionId(),
                history.getQuantity(),
                history.getPrice()
        );
    }

    public static SellerReceiptSimpleInfo convertToSellerReceiptSimpleInfo(BidHistory history) {
        return new SellerReceiptSimpleInfo(
                history.getId(),
                history.getAuctionId(),
                history.getBidStatus(),
                history.getProductName(),
                history.getPrice(),
                history.getQuantity()
        );
    }

    public static BuyerAuctionSimpleInfo convertToBuyerAuctionSimpleInfo(Auction auction) {
        return new BuyerAuctionSimpleInfo(
                auction.getId(),
                auction.getProductName(),
                auction.getCurrentPrice(),
                auction.getStartedAt(),
                auction.getFinishedAt()
        );
    }

    public static SellerAuctionSimpleInfo convertToSellerAuctionSimpleInfo(Auction auction) {
        return new SellerAuctionSimpleInfo(
                auction.getId(),
                auction.getProductName(),
                auction.getOriginPrice(),
                auction.getCurrentPrice(),
                auction.getOriginStock(),
                auction.getCurrentStock(),
                auction.getStartedAt(),
                auction.getFinishedAt()
        );
    }

    public static BidHistory convertToReceipt(ReceiptEntity receiptEntity) {
        return BidHistory.builder()
                .id(receiptEntity.getId())
                .productName(receiptEntity.getProductName())
                .price(receiptEntity.getPrice())
                .quantity(receiptEntity.getQuantity())
                .bidStatus(receiptEntity.getBidStatus())
                .auctionId(receiptEntity.getAuctionId())
                .sellerId(receiptEntity.getSellerId())
                .buyerId(receiptEntity.getBuyerId())
                .createdAt(receiptEntity.getCreatedAt())
                .updatedAt(receiptEntity.getUpdatedAt())
                .build();
    }
}

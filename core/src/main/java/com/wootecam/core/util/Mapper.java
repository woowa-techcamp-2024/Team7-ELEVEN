package com.wootecam.core.util;

import com.wootecam.core.domain.entity.Auction;
import com.wootecam.core.domain.entity.Member;
import com.wootecam.core.domain.entity.Point;
import com.wootecam.core.domain.entity.Receipt;
import com.wootecam.core.dto.auction.info.AuctionInfo;
import com.wootecam.core.dto.auction.info.BuyerAuctionInfo;
import com.wootecam.core.dto.auction.info.BuyerAuctionSimpleInfo;
import com.wootecam.core.dto.auction.info.SellerAuctionInfo;
import com.wootecam.core.dto.auction.info.SellerAuctionSimpleInfo;
import com.wootecam.core.dto.receipt.info.BuyerReceiptSimpleInfo;
import com.wootecam.core.dto.receipt.info.ReceiptInfo;
import com.wootecam.core.dto.receipt.info.SellerReceiptSimpleInfo;
import com.wootecam.core.infra.entity.auction.AuctionEntity;
import com.wootecam.core.infra.entity.member.MemberEntity;
import com.wootecam.core.infra.entity.receipt.ReceiptEntity;
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
                .stock(auction.getCurrentStock())
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .pricePolicy(auction.getPricePolicy())
                .variationDuration(auction.getVariationDuration())
                .startedAt(auction.getStartedAt())
                .finishedAt(auction.getFinishedAt())
                .isShowStock(auction.isShowStock())
                .build();
    }

    public static ReceiptInfo convertToReceiptInfo(Receipt receipt) {
        return ReceiptInfo.builder()
                .receiptId(receipt.getId())
                .productName(receipt.getProductName())
                .price(receipt.getPrice())
                .quantity(receipt.getQuantity())
                .receiptStatus(receipt.getReceiptStatus())
                .auctionId(receipt.getAuctionId())
                .sellerId(receipt.getSellerId())
                .buyerId(receipt.getBuyerId())
                .createdAt(receipt.getCreatedAt())
                .updatedAt(receipt.getUpdatedAt())
                .build();
    }

    public static BuyerAuctionInfo convertToBuyerAuctionInfo(Auction auction) {
        Long currentStock = auction.isShowStock() ? auction.getCurrentStock() : null;
        Long originStock = auction.isShowStock() ? auction.getOriginStock() : null;

        return BuyerAuctionInfo.builder()
                .auctionId(auction.getId())
                .sellerId(auction.getSellerId())
                .productName(auction.getProductName())
                .originPrice(auction.getOriginPrice())
                .currentPrice(auction.getCurrentPrice())
                .originStock(originStock)
                .currentStock(currentStock)
                .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                .pricePolicy(auction.getPricePolicy())
                .variationDuration(auction.getVariationDuration())
                .startedAt(auction.getStartedAt())
                .finishedAt(auction.getFinishedAt())
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

    public static BuyerReceiptSimpleInfo convertToBuyerReceiptSimpleInfo(Receipt history) {
        return BuyerReceiptSimpleInfo.builder()
                .id(history.getId())
                .auctionId(history.getAuctionId())
                .type(history.getReceiptStatus())
                .productName(history.getProductName())
                .price(history.getPrice())
                .quantity(history.getQuantity())
                .build();
    }

    public static SellerReceiptSimpleInfo convertToSellerReceiptSimpleInfo(Receipt history) {
        return SellerReceiptSimpleInfo.builder()
                .id(history.getId())
                .auctionId(history.getAuctionId())
                .type(history.getReceiptStatus())
                .productName(history.getProductName())
                .price(history.getPrice())
                .quantity(history.getQuantity())
                .build();
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

    public static Receipt convertToReceipt(ReceiptEntity receiptEntity) {
        return Receipt.builder()
                .id(receiptEntity.getId())
                .productName(receiptEntity.getProductName())
                .price(receiptEntity.getPrice())
                .quantity(receiptEntity.getQuantity())
                .receiptStatus(receiptEntity.getReceiptStatus())
                .auctionId(receiptEntity.getAuctionId())
                .sellerId(receiptEntity.getSellerId())
                .buyerId(receiptEntity.getBuyerId())
                .createdAt(receiptEntity.getCreatedAt())
                .updatedAt(receiptEntity.getUpdatedAt())
                .build();
    }

    public static Auction convertToAuction(AuctionEntity auctionEntity) {
        return new Auction(
                auctionEntity.getId(),
                auctionEntity.getSellerId(),
                auctionEntity.getProductName(),
                auctionEntity.getOriginPrice(),
                auctionEntity.getCurrentPrice(),
                auctionEntity.getOriginStock(),
                auctionEntity.getCurrentStock(),
                auctionEntity.getMaximumPurchaseLimitCount(),
                auctionEntity.getPricePolicy(),
                auctionEntity.getVariationDuration(),
                auctionEntity.getStartedAt(),
                auctionEntity.getFinishedAt(),
                auctionEntity.isShowStock()
        );
    }

    public static AuctionEntity convertToAuctionEntity(Auction auction) {
        return AuctionEntity.builder()
                .id(auction.getId())
                .sellerId(auction.getSellerId())
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

    public static Member convertToMember(MemberEntity entity) {
        return Member.builder()
                .id(entity.getId())
                .signInId(entity.getSignInId())
                .password(entity.getPassword())
                .role(entity.getRole())
                .point(new Point(entity.getPoint()))
                .build();
    }

    public static MemberEntity convertToMemberEntity(Member member) {
        return MemberEntity.builder()
                .id(member.getId())
                .signInId(member.getSignInId())
                .password(member.getPassword())
                .role(member.getRole())
                .point(member.getPoint().getAmount())
                .build();
    }

    public static ReceiptEntity convertToReceiptEntity(Receipt receipt) {
        return ReceiptEntity.builder()
                .id(receipt.getId())
                .productName(receipt.getProductName())
                .price(receipt.getPrice())
                .quantity(receipt.getQuantity())
                .receiptStatus(receipt.getReceiptStatus())
                .auctionId(receipt.getAuctionId())
                .sellerId(receipt.getSellerId())
                .buyerId(receipt.getBuyerId())
                .createdAt(receipt.getCreatedAt())
                .updatedAt(receipt.getUpdatedAt())
                .build();
    }
}

package com.wootecam.luckyvickyauction.core.auction.repository;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeAuctionRepository implements AuctionRepository {
    private Map<Long, Auction> auctions = new HashMap<>();
    private long id = 1L;

    @Override
    public Auction save(Auction auction) {
        if (auction.getId() == null || auction.getId() == 0L) {
            // 새로운 Auction 객체인 경우
            Long newId = id++;
            Auction newAuction = Auction.builder()
                    .id(newId)
                    .startedAt(auction.getStartedAt())
                    .sellerId(auction.getSellerId())
                    .productName(auction.getProductName())
                    .originPrice(auction.getOriginPrice())
                    .stock(auction.getStock())
                    .maximumPurchaseLimitCount(auction.getMaximumPurchaseLimitCount())
                    .pricePolicy(auction.getPricePolicy())
                    .variationDuration(auction.getVariationDuration())
                    .finishedAt(auction.getFinishedAt())
                    .isShowStock(auction.isShowStock())
                    .auctionStatus(auction.getStatus())
                    .build();
            auctions.put(newId, newAuction);
            return newAuction;
        } else {
            // 기존 Auction 객체를 업데이트하는 경우
            auctions.put(auction.getId(), auction);
            return auction;
        }
    }

    @Override
    public Optional<Auction> findById(Long id) {
        return Optional.ofNullable(auctions.get(id));
    }
}
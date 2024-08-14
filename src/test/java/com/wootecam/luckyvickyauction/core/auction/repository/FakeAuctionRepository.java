package com.wootecam.luckyvickyauction.core.auction.repository;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FakeAuctionRepository implements AuctionRepository {

    private Map<Long, Auction> auctions = new HashMap<>();
    private long id = 1L;

    @Override
    public Auction save(Auction auction) {
        if (Objects.isNull(auction.getId())) {
            Auction newAuction = createNewAuction(auction);
            auctions.put(newAuction.getId(), newAuction);

            return newAuction;
        }
        Auction existsAuction = createExistsAuction(auction);
        auctions.put(existsAuction.getId(), existsAuction);

        return auction;
    }

    private Auction createExistsAuction(Auction auction) {
        return Auction.builder()
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

    private Auction createNewAuction(Auction auction) {
        return Auction.builder()
                .id(id++)
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

    @Override
    public Optional<Auction> findById(Long id) {
        return Optional.ofNullable(auctions.get(id));
    }
}

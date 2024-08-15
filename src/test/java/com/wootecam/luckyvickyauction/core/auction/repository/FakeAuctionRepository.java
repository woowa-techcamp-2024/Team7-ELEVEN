package com.wootecam.luckyvickyauction.core.auction.repository;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public void deleteById(long id) {
        auctions.remove(id);
    }


    // TODO: [AuctionSearchCondition 조건 이후 변경 사항 : FakeRepository의 구현이 condition이 변경되면 수정되어야 합니다.] [writeAt: 2024/08/15/16:30] [writeBy: yudonggeun]
    @Override
    public List<Auction> findAllBy(AuctionSearchCondition condition) {
        return auctions.values().stream()
                .filter(auction -> true)
                .toList();
    }

    // TODO: [SellerAuctionSearchCondition 조건 이후 변경 사항 : FakeRepository의 구현이 condition이 변경되면 수정되어야 합니다.] [writeAt: 2024/08/15/16:30] [writeBy: yudonggeun]
    @Override
    public List<Auction> findAllBy(SellerAuctionSearchCondition condition) {
        return auctions.values().stream()
                .filter(auction -> true)
                .toList();
    }
}

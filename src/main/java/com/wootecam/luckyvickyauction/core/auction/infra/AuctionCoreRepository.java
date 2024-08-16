package com.wootecam.luckyvickyauction.core.auction.infra;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
import java.util.List;
import java.util.Optional;

public class AuctionCoreRepository implements AuctionRepository {
    @Override
    public Auction save(Auction auction) {
        return null;
    }

    @Override
    public Optional<Auction> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(long id) {

    }

    @Override
    public List<Auction> findAllBy(AuctionSearchCondition condition) {
        return List.of();
    }

    @Override
    public List<Auction> findAllBy(SellerAuctionSearchCondition condition) {
        return List.of();
    }
}

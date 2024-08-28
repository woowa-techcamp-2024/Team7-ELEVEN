package com.wootecam.luckyvickyauction.domain.repository;

import com.wootecam.luckyvickyauction.domain.entity.Auction;
import com.wootecam.luckyvickyauction.dto.auction.condition.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.dto.auction.condition.SellerAuctionSearchCondition;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository {

    Auction save(Auction auction);

    Optional<Auction> findById(Long id);

    void deleteById(long id);

    List<Auction> findAllBy(AuctionSearchCondition condition);

    List<Auction> findAllBy(SellerAuctionSearchCondition condition);
}

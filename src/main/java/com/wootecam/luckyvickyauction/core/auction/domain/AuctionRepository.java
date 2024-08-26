package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository {

    Auction save(Auction auction);

    Optional<Auction> findById(Long id);

    void deleteById(long id);

    List<Auction> findAllBy(AuctionSearchCondition condition);

    List<Auction> findAllBy(SellerAuctionSearchCondition condition);

    Optional<Auction> findByIdForUpdate(long auctionId);
}

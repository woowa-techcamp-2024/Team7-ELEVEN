package com.wootecam.luckyvickyauction.core.auction.infra;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import java.util.Optional;

public interface AuctionRepository {
    Auction save(Auction auction);

    Optional<Auction> findById(Long id);

    void deleteById(long id);
}

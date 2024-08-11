package com.wootecam.luckyvickyauction.core.auction.infra;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;

public interface AuctionRepository {
    Auction save(Auction auction);
}

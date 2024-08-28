package com.wootecam.luckyvickyauction.infra.repository.auction;

import com.wootecam.luckyvickyauction.dto.auction.condition.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.dto.auction.condition.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.infra.entity.auction.AuctionEntity;
import java.util.List;

public interface AuctionQueryDslRepository {

    List<AuctionEntity> findAllBy(AuctionSearchCondition condition);

    List<AuctionEntity> findAllBy(SellerAuctionSearchCondition condition);

}

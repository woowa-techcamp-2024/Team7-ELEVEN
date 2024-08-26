package com.wootecam.luckyvickyauction.core.auction.infra;

import com.wootecam.core.dto.auction.condition.AuctionSearchCondition;
import com.wootecam.core.dto.auction.condition.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import java.util.List;

public interface AuctionQueryDslRepository {

    List<AuctionEntity> findAllBy(AuctionSearchCondition condition);

    List<AuctionEntity> findAllBy(SellerAuctionSearchCondition condition);

}

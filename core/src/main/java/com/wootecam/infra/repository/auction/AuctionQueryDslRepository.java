package com.wootecam.infra.repository.auction;

import com.wootecam.core.dto.auction.condition.AuctionSearchCondition;
import com.wootecam.core.dto.auction.condition.SellerAuctionSearchCondition;
import com.wootecam.infra.entity.auction.AuctionEntity;
import java.util.List;

public interface AuctionQueryDslRepository {

    List<AuctionEntity> findAllBy(AuctionSearchCondition condition);

    List<AuctionEntity> findAllBy(SellerAuctionSearchCondition condition);

}

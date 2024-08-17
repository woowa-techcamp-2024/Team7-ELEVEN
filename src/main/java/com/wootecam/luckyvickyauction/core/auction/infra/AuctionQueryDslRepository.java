package com.wootecam.luckyvickyauction.core.auction.infra;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import java.util.List;

public interface AuctionQueryDslRepository {

    List<AuctionEntity> findAllBy(AuctionSearchCondition condition);
}

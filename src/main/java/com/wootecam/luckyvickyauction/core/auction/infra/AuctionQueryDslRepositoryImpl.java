package com.wootecam.luckyvickyauction.core.auction.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import com.wootecam.luckyvickyauction.core.auction.entity.QAuctionEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuctionQueryDslRepositoryImpl implements AuctionQueryDslRepository {

    private final JPAQueryFactory query;

    @Override
    public List<AuctionEntity> findAllBy(AuctionSearchCondition condition) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        return query
                .select(auction)
                .from(auction)
                .limit(condition.size())
                .offset(condition.offset())
                .fetch();
    }
}

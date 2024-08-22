package com.wootecam.luckyvickyauction.core.auction.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
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
                .orderBy(auction.id.desc())
                .limit(condition.size())
                .offset(condition.offset())
                .fetch();
    }

    @Override
    public List<AuctionEntity> findAllBy(SellerAuctionSearchCondition condition) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        return query
                .select(auction)
                .from(auction)
                .where(auction.sellerId.eq(condition.sellerId()))
                .orderBy(auction.id.desc())
                .limit(condition.size())
                .offset(condition.offset())
                .fetch();
    }
}

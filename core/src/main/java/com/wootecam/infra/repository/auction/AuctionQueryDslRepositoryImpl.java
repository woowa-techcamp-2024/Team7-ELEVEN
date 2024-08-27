package com.wootecam.infra.repository.auction;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wootecam.core.dto.auction.condition.AuctionSearchCondition;
import com.wootecam.core.dto.auction.condition.SellerAuctionSearchCondition;
import com.wootecam.infra.entity.auction.AuctionEntity;

import com.wootecam.infra.entity.auction.QAuctionEntity;
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

package com.wootecam.luckyvickyauction.infra.repository.auction;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wootecam.luckyvickyauction.dto.auction.condition.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.dto.auction.condition.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.infra.entity.auction.AuctionEntity;
import com.wootecam.luckyvickyauction.infra.entity.auction.QAuctionEntity;
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

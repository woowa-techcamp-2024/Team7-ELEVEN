package com.wootecam.luckyvickyauction.core.auction.infra;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import com.wootecam.luckyvickyauction.global.util.Mapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuctionCoreRepository implements AuctionRepository {

    private final AuctionJpaRepository auctionJpaRepository;

    @Override
    public Auction save(Auction auction) {
        AuctionEntity auctionEntity = Mapper.convertToAuctionEntity(auction);
        AuctionEntity saved = auctionJpaRepository.save(auctionEntity);
        return Mapper.convertToAuction(saved);
    }

    @Override
    public Optional<Auction> findById(Long id) {
        Optional<AuctionEntity> auction = auctionJpaRepository.findById(id);
        return auction.map(Mapper::convertToAuction);
    }

    @Override
    public Optional<Auction> findByIdAndSellerId(long id, Long sellerId) {
        return auctionJpaRepository.findByIdAndSellerId(id, sellerId)
                .map(Mapper::convertToAuction);
    }

    @Override
    public void deleteById(long id) {
    }

    @Override
    public List<Auction> findAllBy(AuctionSearchCondition condition) {
        return List.of();
    }

    @Override
    public List<Auction> findAllBy(SellerAuctionSearchCondition condition) {
        return List.of();
    }
}

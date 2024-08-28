package com.wootecam.luckyvickyauction.infra.repository.auction;

import com.wootecam.luckyvickyauction.infra.entity.auction.AuctionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionJpaRepository extends JpaRepository<AuctionEntity, Long>, AuctionQueryDslRepository {

    Optional<AuctionEntity> findById(long id);
}

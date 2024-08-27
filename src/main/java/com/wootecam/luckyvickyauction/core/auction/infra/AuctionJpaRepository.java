package com.wootecam.luckyvickyauction.core.auction.infra;

import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionJpaRepository extends JpaRepository<AuctionEntity, Long>, AuctionQueryDslRepository {

    Optional<AuctionEntity> findById(long id);
}

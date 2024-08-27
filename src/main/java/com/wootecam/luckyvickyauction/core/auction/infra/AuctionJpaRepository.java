package com.wootecam.luckyvickyauction.core.auction.infra;

import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface AuctionJpaRepository extends JpaRepository<AuctionEntity, Long>, AuctionQueryDslRepository {

    Optional<AuctionEntity> findById(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AuctionEntity a where a.id = :id")
    Optional<AuctionEntity> findByIdForUpdate(Long id);
}

package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptJpaRepository extends JpaRepository<ReceiptEntity, Long>, ReceiptQueryDslRepository {

    Optional<BidHistory> findByIdAndSellerId(long id, Long sellerId);

    Optional<BidHistory> findByIdAndBuyerId(long id, Long sellerId);
}

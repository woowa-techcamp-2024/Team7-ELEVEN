package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptJpaRepository extends JpaRepository<ReceiptEntity, Long>, ReceiptQueryDslRepository {
}

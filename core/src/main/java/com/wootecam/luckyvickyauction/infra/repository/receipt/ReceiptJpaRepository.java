package com.wootecam.luckyvickyauction.infra.repository.receipt;

import com.wootecam.luckyvickyauction.infra.entity.receipt.ReceiptEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptJpaRepository extends JpaRepository<ReceiptEntity, UUID>, ReceiptQueryDslRepository {
}

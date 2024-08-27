package com.wootecam.infra.repository.receipt;

import com.wootecam.infra.entity.receipt.ReceiptEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptJpaRepository extends JpaRepository<ReceiptEntity, UUID>, ReceiptQueryDslRepository {
}

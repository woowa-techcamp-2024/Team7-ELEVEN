package com.wootecam.luckyvickyauction.infra.repository.receipt;

import com.wootecam.luckyvickyauction.infra.entity.receipt.ReceiptEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ReceiptJpaRepository extends JpaRepository<ReceiptEntity, UUID>, ReceiptQueryDslRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from ReceiptEntity r where r.id = :id")
    Optional<ReceiptEntity> findByIdForUpdate(UUID id);
}

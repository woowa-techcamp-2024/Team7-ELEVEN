package com.wootecam.luckyvickyauction.infra.repository.receipt;

import com.wootecam.luckyvickyauction.domain.entity.Receipt;
import com.wootecam.luckyvickyauction.domain.repository.ReceiptRepository;
import com.wootecam.luckyvickyauction.dto.receipt.condition.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.dto.receipt.condition.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.infra.entity.receipt.ReceiptEntity;
import com.wootecam.luckyvickyauction.util.Mapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReceiptCoreRepository implements ReceiptRepository {

    private final ReceiptJpaRepository receiptJpaRepository;

    @Override
    public Receipt save(Receipt receipt) {
        ReceiptEntity entity = Mapper.convertToReceiptEntity(receipt);
        ReceiptEntity saved = receiptJpaRepository.save(entity);
        return Mapper.convertToReceipt(saved);
    }

    @Override
    public Optional<Receipt> findById(UUID receiptId) {
        Optional<ReceiptEntity> found = receiptJpaRepository.findById(receiptId);
        return found.map(Mapper::convertToReceipt);
    }

    @Override
    public List<Receipt> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition) {
        return receiptJpaRepository.findAllByBuyerId(buyerId, condition).stream()
                .map(Mapper::convertToReceipt)
                .toList();
    }

    @Override
    public List<Receipt> findAllBySellerId(Long sellerId, SellerReceiptSearchCondition condition) {
        return receiptJpaRepository.findAllBySellerId(sellerId, condition).stream()
                .map(Mapper::convertToReceipt)
                .toList();
    }

    @Override
    public Optional<Receipt> findByIdForUpdate(UUID receiptId) {
        Optional<ReceiptEntity> found = receiptJpaRepository.findByIdForUpdate(receiptId);
        return found.map(Mapper::convertToReceipt);
    }
}

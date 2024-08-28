package com.wootecam.luckyvickyauction.domain.repository;

import com.wootecam.luckyvickyauction.domain.entity.Receipt;
import com.wootecam.luckyvickyauction.dto.receipt.condition.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.dto.receipt.condition.SellerReceiptSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReceiptRepository {

    Receipt save(Receipt receipt);

    Optional<Receipt> findById(UUID receiptId);

    List<Receipt> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition);

    List<Receipt> findAllBySellerId(Long sellerId, SellerReceiptSearchCondition condition);

    Optional<Receipt> findByIdForUpdate(UUID receiptId);
}

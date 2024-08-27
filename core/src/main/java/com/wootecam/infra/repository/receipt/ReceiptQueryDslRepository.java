package com.wootecam.infra.repository.receipt;

import com.wootecam.core.dto.receipt.condition.BuyerReceiptSearchCondition;
import com.wootecam.core.dto.receipt.condition.SellerReceiptSearchCondition;
import com.wootecam.infra.entity.receipt.ReceiptEntity;
import java.util.List;

public interface ReceiptQueryDslRepository {

    List<ReceiptEntity> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition);

    List<ReceiptEntity> findAllBySellerId(Long sellerId, SellerReceiptSearchCondition condition);
}

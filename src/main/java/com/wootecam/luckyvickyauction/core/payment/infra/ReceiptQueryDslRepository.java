package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.core.dto.receipt.condition.BuyerReceiptSearchCondition;
import com.wootecam.core.dto.receipt.condition.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import java.util.List;

public interface ReceiptQueryDslRepository {

    List<ReceiptEntity> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition);

    List<ReceiptEntity> findAllBySellerId(Long sellerId, SellerReceiptSearchCondition condition);
}

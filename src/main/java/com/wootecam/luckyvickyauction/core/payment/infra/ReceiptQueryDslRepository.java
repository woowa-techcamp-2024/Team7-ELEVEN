package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import java.util.List;

public interface ReceiptQueryDslRepository {

    List<ReceiptEntity> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition);

    List<ReceiptEntity> findAllBySellerId(Long sellerId, SellerReceiptSearchCondition condition);
}

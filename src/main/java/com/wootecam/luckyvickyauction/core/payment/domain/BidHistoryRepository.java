package com.wootecam.luckyvickyauction.core.payment.domain;

import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import java.util.List;
import java.util.Optional;

public interface BidHistoryRepository {

    BidHistory save(BidHistory bidHistory);

    Optional<BidHistory> findById(long bidHistoryId);

    List<BidHistory> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition);

    List<BidHistory> findAllBySellerId(Long sellerId, SellerReceiptSearchCondition condition);
}

package com.wootecam.luckyvickyauction.core.payment.domain;

import com.wootecam.luckyvickyauction.core.payment.dto.ReceiptSelectCondition;
import java.util.List;
import java.util.Optional;

public interface BidHistoryRepository {

    BidHistory save(BidHistory bidHistory);

    Optional<BidHistory> findById(long bidHistoryId);

    List<BidHistory> findAllBy(ReceiptSelectCondition condition);
}

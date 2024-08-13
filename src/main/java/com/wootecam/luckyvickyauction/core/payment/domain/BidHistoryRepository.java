package com.wootecam.luckyvickyauction.core.payment.domain;

import java.util.Optional;

public interface BidHistoryRepository {

    BidHistory save(BidHistory bidHistory);

    Optional<BidHistory> findById(long bidHistoryId);
}

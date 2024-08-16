package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import java.util.List;
import java.util.Optional;

public class ReceiptCoreRepository implements BidHistoryRepository {
    @Override
    public BidHistory save(BidHistory bidHistory) {
        return null;
    }

    @Override
    public Optional<BidHistory> findById(long bidHistoryId) {
        return Optional.empty();
    }

    @Override
    public List<BidHistory> findAllBy(BuyerReceiptSearchCondition condition) {
        return List.of();
    }
}

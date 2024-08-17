package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.global.util.Mapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReceiptCoreRepository implements BidHistoryRepository {

    private final ReceiptJpaRepository receiptJpaRepository;

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

        return receiptJpaRepository.findAllBy(condition).stream()
                .map(Mapper::convertToReceipt)
                .toList();
    }
}

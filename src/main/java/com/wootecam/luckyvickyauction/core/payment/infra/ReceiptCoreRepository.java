package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
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
        ReceiptEntity entity = Mapper.convertToReceiptEntity(bidHistory);
        ReceiptEntity saved = receiptJpaRepository.save(entity);
        return Mapper.convertToReceipt(saved);
    }

    @Override
    public Optional<BidHistory> findById(long bidHistoryId) {
        Optional<ReceiptEntity> found = receiptJpaRepository.findById(bidHistoryId);
        return found.map(Mapper::convertToReceipt);
    }

    @Override
    public Optional<BidHistory> findByIdAndMemberId(long bidHistoryId, Long memberId, Role role) {
        if (role.equals(Role.SELLER)) {
            return receiptJpaRepository.findByIdAndSellerId(bidHistoryId, memberId);
        }
        return receiptJpaRepository.findByIdAndBuyerId(bidHistoryId, memberId);
    }

    @Override
    public List<BidHistory> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition) {
        return receiptJpaRepository.findAllByBuyerId(buyerId, condition).stream()
                .map(Mapper::convertToReceipt)
                .toList();
    }

    @Override
    public List<BidHistory> findAllBy(SellerReceiptSearchCondition condition) {
        return receiptJpaRepository.findAllBy(condition).stream()
                .map(Mapper::convertToReceipt)
                .toList();
    }
}

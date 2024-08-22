package com.wootecam.luckyvickyauction.core.payment.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.entity.QReceiptEntity;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReceiptQueryDslRepositoryImpl implements ReceiptQueryDslRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<ReceiptEntity> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition) {
        QReceiptEntity receipt = QReceiptEntity.receiptEntity;

        return factory
                .select(receipt)
                .from(receipt)
                .where(receipt.buyerId.eq(buyerId))
                .limit(condition.size())
                .offset(condition.offset())
                .fetch();
    }

    @Override
    public List<ReceiptEntity> findAllBySellerId(Long sellerId, SellerReceiptSearchCondition condition) {
        QReceiptEntity receipt = QReceiptEntity.receiptEntity;

        return factory
                .select(receipt)
                .from(receipt)
                .where(receipt.sellerId.eq(sellerId))
                .limit(condition.size())
                .offset(condition.offset())
                .fetch();
    }
}

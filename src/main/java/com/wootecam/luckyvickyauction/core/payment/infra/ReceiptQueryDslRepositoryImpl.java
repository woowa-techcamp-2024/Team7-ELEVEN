package com.wootecam.luckyvickyauction.core.payment.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.entity.QReceiptEntity;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;

// TODO [추후 페이지네이션 구현] [writeAt: 2024/08/17/19:03] [writeBy: minseok-oh]
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
                .fetch();
    }

    @Override
    public List<ReceiptEntity> findAllBy(Long sellerId, SellerReceiptSearchCondition condition) {
        QReceiptEntity receipt = QReceiptEntity.receiptEntity;

        return factory
                .select(receipt)
                .from(receipt)
                .where(receipt.sellerId.eq(sellerId))
                .limit(condition.size())
                .fetch();
    }
}

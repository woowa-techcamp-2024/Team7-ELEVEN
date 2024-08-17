package com.wootecam.luckyvickyauction.core.payment.infra;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReceiptQueryDslRepositoryImpl implements ReceiptQueryDslRepository {

    private final JPAQueryFactory factory;

    @Override
    public List<ReceiptEntity> findAllBy(BuyerReceiptSearchCondition condition) {
        return null;
    }
}

package com.wootecam.luckyvickyauction.core.payment.dto;

import lombok.Builder;

/**
 * 거래 내역 목록 조회시 적용할 수 있는 조건을 나타내는 dto 입니다.
 */
public class TxHistorySelectCondition {

    @Builder
    private TxHistorySelectCondition() {
    }
}

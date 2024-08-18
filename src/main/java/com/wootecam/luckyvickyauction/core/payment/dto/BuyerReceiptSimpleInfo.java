package com.wootecam.luckyvickyauction.core.payment.dto;

import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import lombok.Builder;

/**
 * 구매자가 거래 내역 목록을 조회할 때, 노출되는 각 내역의 데이터를 나타내는 dto 입니다.
 *
 * @param id        거내 내역 식별자입니다.
 * @param type      거래 타입 {@link BidStatus}
 * @param auctionId 구매한 경매의 식별자입니다.
 * @param quantity  구매 수량
 * @param price     구매 가격
 */
@Builder
public record BuyerReceiptSimpleInfo(
        long id,
        BidStatus type,
        long auctionId,
        long quantity,
        long price
) {
}

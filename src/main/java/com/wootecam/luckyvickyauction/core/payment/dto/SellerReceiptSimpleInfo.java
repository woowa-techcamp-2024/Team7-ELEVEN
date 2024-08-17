package com.wootecam.luckyvickyauction.core.payment.dto;

import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;

/**
 * 판매자가 자신의 경매와 관련있는 거래 내역 목록을 조회 시 사용하는 dto
 *
 * @param id          거래 내역 식별자
 * @param auctionId   경매 식별자
 * @param type        거래 타입 {@link BidStatus}
 * @param productName 상품명
 * @param price       거래 가격
 * @param quantity    거래 수량
 */
public record SellerReceiptSimpleInfo(
        Long id,
        Long auctionId,
        BidStatus type,
        String productName,
        long price,
        long quantity
) {
}

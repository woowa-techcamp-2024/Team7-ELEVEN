package com.wootecam.core.dto.receipt.info;

import com.wootecam.core.domain.entity.type.ReceiptStatus;
import java.util.UUID;
import lombok.Builder;

/**
 * 판매자가 자신의 경매와 관련있는 거래 내역 목록을 조회 시 사용하는 dto
 *
 * @param id          거래 내역 식별자
 * @param auctionId   경매 식별자
 * @param type        거래 타입 {@link ReceiptStatus}
 * @param productName 상품명
 * @param price       거래 가격
 * @param quantity    거래 수량
 */
@Builder
public record SellerReceiptSimpleInfo(
        UUID id,
        Long auctionId,
        ReceiptStatus type,
        String productName,
        long price,
        long quantity
) {
}

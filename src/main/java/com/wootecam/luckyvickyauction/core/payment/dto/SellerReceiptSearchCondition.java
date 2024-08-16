package com.wootecam.luckyvickyauction.core.payment.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;

/**
 * 판매자가 거래 내역 목록 조회시 적용할 수 있는 조건을 나타내는 dto 입니다.
 *
 * @param sellerId 거래 내역을 조회할 판매자의 식별자
 * @param size     조회할 거래 내역의 개수 (default: 10) (Min: 1, Max: 100)
 */
public record SellerReceiptSearchCondition(
        Long sellerId,
        int size
) {

    public SellerReceiptSearchCondition {
        pageSizeShouldBeBetweenOneAndHundred(size);
    }

    private void pageSizeShouldBeBetweenOneAndHundred(int size) {
        if (size < 1 || size > 100) {
            throw new BadRequestException("size는 1 이상 100 이하의 값이어야 합니다.", ErrorCode.G001);
        }
    }

}
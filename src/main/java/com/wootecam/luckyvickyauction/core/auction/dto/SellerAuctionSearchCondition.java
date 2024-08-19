package com.wootecam.luckyvickyauction.core.auction.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;

public record SellerAuctionSearchCondition(
        long sellerId,
        int offset,
        int size
) {
    public SellerAuctionSearchCondition {
        validateSizeBetween(1, 100, size);
    }

    private void validateSizeBetween(int from, int to, int size) {
        if (size < from || size > to) {
            throw new BadRequestException("size는 " + from + " 이상 " + to + " 이하의 값이어야 합니다.", ErrorCode.G001);
        }
    }
}

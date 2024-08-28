package com.wootecam.luckyvickyauction.dto.auction.condition;

import com.wootecam.luckyvickyauction.exception.BadRequestException;
import com.wootecam.luckyvickyauction.exception.ErrorCode;

// 조회할때 조건
public record AuctionSearchCondition(
        int offset,
        int size
) {
    public AuctionSearchCondition {
        validateSizeBetween(1, 100, size);
    }

    private void validateSizeBetween(int from, int to, int size) {
        if (size < from || size > to) {
            throw new BadRequestException("size는 " + from + " 이상 " + to + " 이하의 값이어야 합니다. 현재요청: " + size,
                    ErrorCode.G001);
        }
    }
}

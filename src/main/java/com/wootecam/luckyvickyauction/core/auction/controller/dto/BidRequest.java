package com.wootecam.luckyvickyauction.core.auction.controller.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;

public record BidRequest(
        long price,
        long quantity
) {
    public BidRequest {
        validatePrice(price);
        validateQuantity(quantity);
    }

    private void validatePrice(long price) {
        if (price < 0) {
            throw new BadRequestException("경매 입찰 요청 가격은 음수일 수 없습니다. 요청가격: " + price, ErrorCode.A033);
        }
    }

    private void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("경매 입찰 요청 수량은 0보다 커야합니다. 요청수량: " + quantity, ErrorCode.A034);
        }
    }
}

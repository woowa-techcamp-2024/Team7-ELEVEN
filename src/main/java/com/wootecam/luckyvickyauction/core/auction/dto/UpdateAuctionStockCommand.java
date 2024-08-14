package com.wootecam.luckyvickyauction.core.auction.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.ZonedDateTime;
import java.util.Objects;

public record UpdateAuctionStockCommand(
        ZonedDateTime requestTime,
        Long auctionId,
        long changeRequestStock
) {

    private static final String ERROR_NULL_VALUE = "%s(은)는 Null일 수 없습니다.";

    public UpdateAuctionStockCommand {
        validateNotNull(requestTime, "요청 시간");
        validateNotNull(auctionId, "경매 ID");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.A007);
        }
    }
}

package com.wootecam.luckyvickyauction.core.auction.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;

public record CancelAuctionCommand(
        LocalDateTime requestTime,
        long auctionId
) {

    private static final String ERROR_NULL_VALUE = "%s(은)는 Null일 수 없습니다.";

    public CancelAuctionCommand {
        validateNotNull(requestTime, "requestTime");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }
}

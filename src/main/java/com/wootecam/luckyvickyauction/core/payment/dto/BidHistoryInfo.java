package com.wootecam.luckyvickyauction.core.payment.dto;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record BidHistoryInfo(
        long bidHistoryId,
        String productName,
        long price,
        long quantity,
        BidStatus bidStatus,
        long auctionId,
        Member seller,
        Member buyer,
        ZonedDateTime createdAt
) {

    public static final String ERROR_PRODUCT_NAME = "상품 이름은 비어있을 수 없습니다.";
    public static final String ERROR_PRICE = "거래 가격은 0보다 커야 합니다. 입찰 가격: %d";
    public static final String ERROR_QUANTITY = "수량은 0보다 커야 합니다. 수량: %d";
    public static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public BidHistoryInfo {
        validateNotNull(productName, "상품 이름");
        validateNotNull(bidStatus, "입찰 상태");
        validateNotNull(seller, "판매자 정보");
        validateNotNull(buyer, "구매자 정보");
        validateNotNull(createdAt, "거래 일자");

        validateProductName(productName);
        validatePrice(price);
        validateQuantity(quantity);
    }

    private static void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new BadRequestException(String.format(ERROR_QUANTITY, quantity), ErrorCode.B004);
        }
    }

    private void validateProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new BadRequestException(ERROR_PRODUCT_NAME, ErrorCode.B002);
        }
    }

    private void validatePrice(long price) {
        if (price <= 0) {
            throw new BadRequestException(String.format(ERROR_PRICE, price), ErrorCode.B003);
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }
}

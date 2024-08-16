package com.wootecam.luckyvickyauction.core.payment.dto;

import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;

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

    public static final String ERROR_PRODUCT_NAME = "상품 이름은 비어있을 수 없습니다.";
    public static final String ERROR_PRICE = "거래 가격은 0보다 커야 합니다. 입찰 가격: %d";
    public static final String ERROR_QUANTITY = "수량은 0보다 커야 합니다. 수량: %d";
    public static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public SellerReceiptSimpleInfo {
        validateNotNull(type, "거래 타입");
        validateNotNull(productName, "상품 이름");

        validateProductName(productName);
        validatePrice(price);
        validateQuantity(quantity);
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

    private void validateQuantity(long quantity) {
        if (quantity <= 0) {
            throw new BadRequestException(String.format(ERROR_QUANTITY, quantity), ErrorCode.B004);
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }
}

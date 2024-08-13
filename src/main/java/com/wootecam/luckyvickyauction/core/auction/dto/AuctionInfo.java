package com.wootecam.luckyvickyauction.core.auction.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import lombok.Builder;

/**
 * @param sellerId                  판매자의 ID
 * @param productName               판매상품의 이름
 * @param originPrice               판매상품의 원래 가격
 * @param currentPrice              현재 경매에 설정된 가격
 * @param stock                     현재 경매에 남은 재고 개수
 * @param maximumPurchaseLimitCount 최대 구매 가능한 개수
 * @param isShowStock               재고를 보여줄지 여부
 */
@Builder
public record AuctionInfo(Long auctionId, Long sellerId, String productName, long originPrice, long currentPrice,
                          long stock,
                          int maximumPurchaseLimitCount, boolean isShowStock) {

    public static final String ERROR_PRODUCT_NAME = "상품 이름은 비어있을 수 없습니다.";
    public static final String ERROR_ORIGIN_PRICE = "상품 원가는 0보다 커야 합니다. 상품 원가: %d";
    public static final String ERROR_CURRENT_PRICE = "현재 가격은 0보다 커야 합니다. 현재 가격: %d";
    public static final String ERROR_STOCK = "재고는 0보다 작을 수 없습니다. 재고: %d";
    public static final String ERROR_MAXIMUM_PURCHASE_LIMIT_COUNT = "최대 구매 수량 제한은 0보다 커야 합니다. 최대 구매 수량 제한: %d";
    public static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public AuctionInfo {
        validateNotNull(auctionId, "경매 ID");
        validateNotNull(sellerId, "판매자 ID");
        validateNotNull(productName, "상품 이름");

        validateProductName(productName);
        validateOriginPrice(originPrice);
        validateCurrentPrice(currentPrice);
        validateStock(stock);
        validateMaximumPurchaseLimitCount(maximumPurchaseLimitCount);
    }

    private void validateProductName(String productName) {
        if (productName.isEmpty()) {
            throw new BadRequestException(ERROR_PRODUCT_NAME, ErrorCode.A001);
        }
    }

    private void validateOriginPrice(long originPrice) {
        if (originPrice <= 0) {
            throw new BadRequestException(String.format(ERROR_ORIGIN_PRICE, originPrice), ErrorCode.A002);
        }
    }

    private void validateCurrentPrice(long currentPrice) {
        if (currentPrice <= 0) {
            throw new BadRequestException(String.format(ERROR_CURRENT_PRICE, currentPrice), ErrorCode.A013);
        }
    }

    private void validateStock(long stock) {
        if (stock < 0) {
            throw new BadRequestException(String.format(ERROR_STOCK, stock), ErrorCode.A000);
        }
    }

    private void validateMaximumPurchaseLimitCount(int maximumPurchaseLimitCount) {
        if (maximumPurchaseLimitCount <= 0) {
            throw new BadRequestException(String.format(ERROR_MAXIMUM_PURCHASE_LIMIT_COUNT, maximumPurchaseLimitCount),
                    ErrorCode.A003);
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.A007);
        }
    }
}

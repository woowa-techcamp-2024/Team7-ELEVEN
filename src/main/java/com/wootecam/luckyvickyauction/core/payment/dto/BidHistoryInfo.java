package com.wootecam.luckyvickyauction.core.payment.dto;

import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
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
        long sellerId,
        long buyerId,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {

    // TODO [응답용 Dto에 가까워서 검증 로직을 제거하였습니다. 주석의 내용을 컨트롤러에서 구현해야하기 때문에 주석으로 표시합니다.] [2021/08/15/14:18] yudonggeun
//    public static final String ERROR_PRODUCT_NAME = "상품 이름은 비어있을 수 없습니다.";
//    public static final String ERROR_PRICE = "거래 가격은 0보다 커야 합니다. 입찰 가격: %d";
//    public static final String ERROR_QUANTITY = "수량은 0보다 커야 합니다. 수량: %d";
//    public static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";
//
//    public BidHistoryInfo {
//        validateNotNull(productName, "상품 이름");
//        validateNotNull(bidStatus, "입찰 상태");
//        validateNotNull(sellerId, "판매자 정보");
//        validateNotNull(buyerId, "구매자 정보");
//        validateNotNull(createdAt, "거래 일자");
//        validateNotNull(updatedAt, "변경 일자");
//
//        validateProductName(productName);
//        validatePrice(price);
//        validateQuantity(quantity);
//    }
//
//    private static void validateQuantity(long quantity) {
//        if (quantity <= 0) {
//            throw new BadRequestException(String.format(ERROR_QUANTITY, quantity), ErrorCode.B004);
//        }
//    }
//
//    private void validateProductName(String productName) {
//        if (productName == null || productName.isBlank()) {
//            throw new BadRequestException(ERROR_PRODUCT_NAME, ErrorCode.B002);
//        }
//    }
//
//    private void validatePrice(long price) {
//        if (price <= 0) {
//            throw new BadRequestException(String.format(ERROR_PRICE, price), ErrorCode.B003);
//        }
//    }
//
//    private void validateNotNull(Object value, String fieldName) {
//        if (value == null) {
//            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
//        }
//    }
}

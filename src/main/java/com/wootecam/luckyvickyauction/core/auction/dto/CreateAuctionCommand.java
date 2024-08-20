package com.wootecam.luckyvickyauction.core.auction.dto;

// 명령 단위 : 옥션 생성할때 쓰기 위한 파라미터 들

import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 경매 생성을 위한 명령 객체
 *
 * @param productName               상품 이름
 * @param originPrice               상품 원가
 * @param stock                     재고 수량
 * @param maximumPurchaseLimitCount 최대 구매 제한 수량 (인당 구매 가능 수량)
 * @param pricePolicy               경매 유형 {@link PricePolicy}
 * @param variationDuration         가격 변동 주기
 * @param requestTime               요청 시간
 * @param startedAt                 경매 시작 시간
 * @param finishedAt                경매 종료 시간
 */

public record CreateAuctionCommand(
        String productName,
        long originPrice,
        long stock,
        long maximumPurchaseLimitCount,
        PricePolicy pricePolicy,
        Duration variationDuration,
        LocalDateTime requestTime,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        boolean isShowStock
) {
    private static final String ERROR_PRODUCT_NAME = "상품 이름은 비어있을 수 없습니다.";
    private static final String ERROR_ORIGIN_PRICE = "상품 원가는 0보다 커야 합니다. 상품 원가: %d";
    private static final String ERROR_MAX_PURCHASE_LIMIT = "최대 구매 수량 제한은 0보다 커야 합니다. 최대 구매 수량 제한: %d";
    private static final String ERROR_VARIATION_DURATION = "변동 시간 단위는 0보다 커야 합니다. 변동 시간: %s";
    private static final String ERROR_AUCTION_TIME = "경매의 시작 시간은 종료 시간보다 이전이어야 합니다. 시작 시간: %s, 종료 시간: %s";
    private static final String ERROR_STARTED_AT = "경매의 시작시간은 반드시 요청 시간보다 늦어야 합니다. 요청 시간: %s, 시작 시간: %s";
    private static final String ERROR_STOCK = "재고는 인당 구매 수량보다 작을 수 없습니다. 재고: %d, 인당 구매 수량: %d";
    private static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public CreateAuctionCommand {
        validateNotNull(productName, "상품 이름");
        validateNotNull(pricePolicy, "경매 유형");
        validateNotNull(variationDuration, "가격 변동 주기");
        validateNotNull(requestTime, "요청 시간");
        validateNotNull(startedAt, "경매 시작 시간");
        validateNotNull(finishedAt, "경매 종료 시간");

        validateProductName(productName);
        validateOriginPrice(originPrice);
        validateMaximumPurchaseLimitCount(maximumPurchaseLimitCount);
        validateVariationDuration(variationDuration);
        validateAuctionTime(startedAt, finishedAt);
        validateStartedAt(requestTime, startedAt);
        validateStock(stock, maximumPurchaseLimitCount);
    }

    private void validateProductName(String productName) {
        if (productName.trim().isEmpty()) {
            throw new BadRequestException(ERROR_PRODUCT_NAME, ErrorCode.A001);
        }
    }

    private void validateOriginPrice(long originPrice) {
        if (originPrice <= 0) {
            throw new BadRequestException(String.format(ERROR_ORIGIN_PRICE, originPrice), ErrorCode.A002);
        }
    }

    private void validateMaximumPurchaseLimitCount(long maximumPurchaseLimitCount) {
        if (maximumPurchaseLimitCount <= 0) {
            throw new BadRequestException(String.format(ERROR_MAX_PURCHASE_LIMIT, maximumPurchaseLimitCount),
                    ErrorCode.A003);
        }
    }

    private void validateVariationDuration(Duration variationDuration) {
        if (variationDuration.isNegative() || variationDuration.isZero()) {
            throw new BadRequestException(String.format(ERROR_VARIATION_DURATION, variationDuration), ErrorCode.A005);
        }
    }

    private void validateAuctionTime(LocalDateTime startedAt, LocalDateTime finishedAt) {
        if (!startedAt.isBefore(finishedAt)) {
            throw new BadRequestException(String.format(ERROR_AUCTION_TIME, startedAt, finishedAt), ErrorCode.A006);
        }
    }

    private void validateStartedAt(LocalDateTime nowAt, LocalDateTime startedAt) {
        if (startedAt.isBefore(nowAt)) {
            String message = String.format(ERROR_STARTED_AT, nowAt, startedAt);
            throw new BadRequestException(message, ErrorCode.A014);
        }
    }

    private void validateStock(long stock, long maximumPurchaseLimitCount) {
        if (stock < maximumPurchaseLimitCount) {
            throw new BadRequestException(String.format(ERROR_STOCK, stock, maximumPurchaseLimitCount), ErrorCode.A000);
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }
}

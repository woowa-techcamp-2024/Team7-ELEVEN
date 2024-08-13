package com.wootecam.luckyvickyauction.core.auction.dto;

import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.Builder;

/**
 * 경매 생성을 위한 명령 객체
 *
 * @param auctionId                 경매 ID
 * @param sellerId                  변경을 요청하는 판매자 ID
 * @param originPrice               상품 원가
 * @param stock                     재고 수량
 * @param maximumPurchaseLimitCount 최대 구매 제한 수량 (인당 구매 가능 수량)
 * @param pricePolicy               경매 유형 {@link PricePolicy}
 * @param variationDuration         가격 변동 주기
 * @param startedAt                 경매 시작 시간
 * @param finishedAt                경매 종료 시간
 * @param requestTime               사용자가 요청한 시간
 */
@Builder
public record UpdateAuctionCommand(
        Long auctionId,
        Long sellerId,
        long originPrice,
        int stock,
        int maximumPurchaseLimitCount,
        PricePolicy pricePolicy,
        Duration variationDuration,
        ZonedDateTime startedAt,
        ZonedDateTime finishedAt,
        Boolean isShowStock,
        // command meta info
        ZonedDateTime requestTime
) {
    private static final String ERROR_MAX_PURCHASE_LIMIT = "최대 구매 수량 제한은 0보다 커야 합니다. 최대 구매 수량 제한: %d";
    private static final String ERROR_VARIATION_DURATION = "변동 시간 단위는 0보다 커야 합니다. 변동 시간: %s";
    private static final String ERROR_AUCTION_TIME = "경매의 시작 시간은 종료 시간보다 이전이어야 합니다. 시작 시간: %s, 종료 시간: %s";
    private static final String ERROR_STOCK = "재고는 인당 구매 수량보다 작을 수 없습니다. 재고: %d, 인당 구매 수량: %d";
    private static final String ERROR_ORIGIN_PRICE = "상품 원가는 0보다 커야 합니다. 상품 원가: %d";
    private static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public UpdateAuctionCommand {
        validateNotNull(auctionId, "경매 ID");
        validateNotNull(sellerId, "판매자 ID");
        validateNotNull(pricePolicy, "경매 유형");
        validateNotNull(variationDuration, "가격 변동 주기");
        validateNotNull(startedAt, "경매 시작 시간");
        validateNotNull(finishedAt, "경매 종료 시간");
        validateNotNull(requestTime, "요청 시간");

        validateOriginPrice(originPrice);
        validateMaximumPurchaseLimitCount(maximumPurchaseLimitCount);
        validateVariationDuration(variationDuration);
        validateAuctionTime(startedAt, finishedAt);
        validateStock(stock, maximumPurchaseLimitCount);
    }

    private void validateOriginPrice(long originPrice) {
        if (originPrice <= 0) {
            throw new BadRequestException(String.format(ERROR_ORIGIN_PRICE, originPrice), ErrorCode.A002);
        }
    }

    private void validateMaximumPurchaseLimitCount(int maximumPurchaseLimitCount) {
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

    private void validateAuctionTime(ZonedDateTime startedAt, ZonedDateTime finishedAt) {
        if (!startedAt.isBefore(finishedAt)) {
            throw new BadRequestException(String.format(ERROR_AUCTION_TIME, startedAt, finishedAt), ErrorCode.A006);
        }
    }

    private void validateStock(int stock, int maximumPurchaseLimitCount) {
        if (stock < maximumPurchaseLimitCount) {
            throw new BadRequestException(String.format(ERROR_STOCK, stock, maximumPurchaseLimitCount), ErrorCode.A000);
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.A007);
        }
    }
}

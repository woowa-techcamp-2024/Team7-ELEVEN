package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.Duration;
import java.time.ZonedDateTime;

public class ConstantPricePolicy implements PricePolicy {

    private final long variationWidth;

    public ConstantPricePolicy(long variationWidth) {
        validateVariationWidth(variationWidth);
        this.variationWidth = variationWidth;
    }

    private void validateVariationWidth(long variationWidth) {
        if (variationWidth <= 0) {
            throw new BadRequestException("가격 변동폭은 0보다 커야 합니다.", ErrorCode.A004);
        }
    }

    @Override
    public void validate(ZonedDateTime startedAt, ZonedDateTime finishedAt, Duration variationDuration, long price) {
        validateVariationWidthOverPrice(price);
        validateZeroPrice(startedAt, finishedAt, variationDuration, price);
    }

    private void validateVariationWidthOverPrice(long price) {
        if (price <= variationWidth) {
            throw new BadRequestException(
                    String.format("상품 원가는 가격 변동폭보다 커야 합니다. 상품 원가: %d, 가격 변동폭: %d", price, variationWidth),
                    ErrorCode.A009);
        }
    }

    private void validateZeroPrice(ZonedDateTime startedAt, ZonedDateTime finishedAt, Duration variationDuration,
                                   long price) {
        Duration totalDuration = Duration.between(startedAt, finishedAt);
        long numberOfVariations = totalDuration.dividedBy(variationDuration);

        // 마지막 변동을 제외한 가격 하락 계산
        long totalPriceDecrease = (numberOfVariations - 1) * variationWidth;

        if (price <= totalPriceDecrease) {
            String message = String.format("경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 총 가격 하락: %d", price, totalPriceDecrease);
            throw new BadRequestException(message, ErrorCode.A028);
        }
    }
}

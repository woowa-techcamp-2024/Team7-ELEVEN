package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;

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
    public long applyWholeDiscount(long variationCount, long price) {
        validateVariationWidthOverPrice(price);

        long totalPriceDecrease = variationCount * variationWidth;

        return price - totalPriceDecrease;
    }

    private void validateVariationWidthOverPrice(long price) {
        if (price <= variationWidth) {
            throw new BadRequestException(
                    String.format("상품 원가는 가격 변동폭보다 커야 합니다. 상품 원가: %d, 가격 변동폭: %d", price, variationWidth),
                    ErrorCode.A009);
        }
    }
}

package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;

public class ConstantPricePolicy implements PricePolicy {

    private final long variationWidth;

    public ConstantPricePolicy(long variationWidth) {
        this.variationWidth = variationWidth;
    }

    @Override
    public void validate(long price) {
        if (price <= variationWidth) {
            throw new BadRequestException(
                    String.format("상품 원가는 가격 변동폭보다 커야 합니다. 상품 원가: %d, 가격 변동폭: %d", price, variationWidth),
                    ErrorCode.A009);
        }
    }
}

package com.wootecam.core.domain.type;

import com.wootecam.core.exception.BadRequestException;
import com.wootecam.core.exception.ErrorCode;
import java.util.Objects;
import lombok.Getter;

@Getter
public class ConstantPricePolicy implements PricePolicy {

    private PricePolicyType type = PricePolicyType.CONSTANT;
    private long variationWidth;

    public ConstantPricePolicy() {
    }

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
    public long calculatePriceAtVariation(long price, long variationCount) {
        validateVariationWidthOverPrice(price);

        return price - variationCount * variationWidth;
    }

    private void validateVariationWidthOverPrice(long price) {
        if (price <= variationWidth) {
            throw new BadRequestException(
                    String.format("상품 원가는 가격 변동폭보다 커야 합니다. 상품 원가: %d, 가격 변동폭: %d", price, variationWidth),
                    ErrorCode.A008);
        }
    }

    @Override
    public PricePolicyType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConstantPricePolicy that = (ConstantPricePolicy) o;
        return variationWidth == that.variationWidth && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, variationWidth);
    }
}

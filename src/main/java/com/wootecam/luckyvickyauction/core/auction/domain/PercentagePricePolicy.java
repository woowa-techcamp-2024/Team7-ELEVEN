package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class PercentagePricePolicy implements PricePolicy {

    private final double discountRate;

    public PercentagePricePolicy(double discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public void validate(long price) {
        // todo 데이터 손실 가능성
        long nextPrice = (long) (price * (100 - discountRate)) / 100;

        if (nextPrice <= 0) {
            throw new BadRequestException(
                    String.format("상품 원가는 할인율을 적용한 가격보다 커야 합니다. 상품 원가: %d, 할인율: %f", price, discountRate),
                    ErrorCode.A010);
        }
    }
}

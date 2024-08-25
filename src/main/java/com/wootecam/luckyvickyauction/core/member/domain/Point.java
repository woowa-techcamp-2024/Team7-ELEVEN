package com.wootecam.luckyvickyauction.core.member.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class Point {

    private long amount;

    public Point(final long amount) {
        validatePositiveAmount(amount);
        this.amount = amount;
    }

    public void minus(final long minusAmount) {
        validatePositiveAmount(minusAmount);
        if (amount < minusAmount) {
            throw new BadRequestException("포인트가 부족합니다.", ErrorCode.P001);
        }
        amount -= minusAmount;
    }

    public void plus(final long price) {
        validatePositiveAmount(price);
        if (price > 0 && amount > Long.MAX_VALUE - price) {
            throw new BadRequestException("포인트가 최대치를 초과하였습니다.", ErrorCode.P006);
        }
        amount += price;
    }

    private void validatePositiveAmount(final long amount) {
        if (amount < 0) {
            throw new BadRequestException("금액은 양수여야 합니다.", ErrorCode.P008);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point point = (Point) o;
        return getAmount() == point.getAmount();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getAmount());
    }
}

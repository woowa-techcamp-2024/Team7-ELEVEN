package com.wootecam.luckyvickyauction.core.member.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class Point {

    private long amount;

    public Point(final long amount) {
        this.amount = amount;
    }

    public void minus(final long minusAmount) {
        if (amount < minusAmount) {
            throw new BadRequestException("포인트가 부족합니다.", ErrorCode.P001);
        }
        amount -= minusAmount;
    }

    public void plus(final long price) {
        amount += price;
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

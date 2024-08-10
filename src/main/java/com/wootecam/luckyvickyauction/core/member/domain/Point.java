package com.wootecam.luckyvickyauction.core.member.domain;

public class Point {
    private long amount;

    public Point(final long amount) {
        this.amount = amount;
    }

    public boolean isEnoughToBuy(final Long price, final int quantity) {
        return amount >= price * quantity;
    }
}

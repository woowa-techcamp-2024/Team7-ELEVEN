package com.wootecam.luckyvickyauction.core.member.domain;

public class Buyer extends Member {

    public Buyer(final String signInId, final Role role, final Point point) {
        super(signInId, role, point);
    }

    public void usePoint(final long price, final int quantity) {
        point.minus(price * quantity);
    }
}

package com.wootecam.luckyvickyauction.core.member.domain;

public class Buyer extends Member {

    public Buyer(final String signInId, final Role role, final Point point) {
        super(signInId, role, point);
    }

    public boolean canBuy(final Long price, final int quantity) {
        return point.isEnoughToBuy(price, quantity);
    }
}

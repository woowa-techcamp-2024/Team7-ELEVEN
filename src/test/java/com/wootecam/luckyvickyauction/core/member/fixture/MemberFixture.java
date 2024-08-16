package com.wootecam.luckyvickyauction.core.member.fixture;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;

public class MemberFixture {
    private MemberFixture() {
    }

    public static Member createBuyerWithDefaultPoint() {
        return Member.builder()
                .id(1L)
                .signInId("buyer")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(1000L))
                .build();
    }

    public static Member createSellerWithDefaultPoint() {
        return Member.builder()
                .id(1L)
                .signInId("seller")
                .password("password00")
                .role(Role.SELLER)
                .point(new Point(1000L))
                .build();
    }
}

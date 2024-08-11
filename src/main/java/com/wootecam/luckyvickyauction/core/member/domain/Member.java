package com.wootecam.luckyvickyauction.core.member.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {

    private Long id;
    private String signInId;
    private Role role;
    protected Point point;

    @Builder
    public Member(final String signInId, final Role role, final Point point) {
        this.signInId = signInId;
        this.role = role;
        this.point = point;
    }

    public static Member createMemberWithRole(final String signInId, final String userRole) {
        Role role = Role.find(userRole);

        return new Member(signInId, role, new Point(0L));
    }

    public void usePoint(final long price) {
        point.minus(price);
    }

    public void chargePoint(final long price) {
        point.plus(price);
    }

    public boolean isBuyer() {
        return role.equals(Role.BUYER);
    }
}

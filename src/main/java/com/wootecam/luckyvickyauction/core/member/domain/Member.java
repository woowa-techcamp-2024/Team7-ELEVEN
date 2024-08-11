package com.wootecam.luckyvickyauction.core.member.domain;

import lombok.Getter;

@Getter
public abstract class Member {

    private Long id;
    private String signInId;
    private Role role;
    protected Point point;

    public Member(final String signInId, final Role role, final Point point) {
        this.signInId = signInId;
        this.role = role;
        this.point = point;
    }

    public static Member createMemberWithRole(final String signInId, final String userRole) {
        Role role = Role.find(userRole);
        if (role.equals(Role.BUYER)) {
            return new Buyer(signInId, role, new Point(0L));
        }
        return new Seller(signInId, role, new Point(0L));
    }

    public boolean isBuyer() {
        return role.equals(Role.BUYER);
    }
}

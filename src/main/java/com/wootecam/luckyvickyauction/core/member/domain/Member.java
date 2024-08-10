package com.wootecam.luckyvickyauction.core.member.domain;

public abstract class Member {

    private String signInId;
    private Role role;
    private Point point;

    public Member(final String signInId, final Role role, final Point point) {
        this.signInId = signInId;
        this.role = role;
        this.point = point;
    }

    public static Member createMemberWithRole(final String signInId, final String userRole) {
        Role role = Role.find(userRole);
        if (role.equals(Role.BUYER)) {
            return new Buyer(signInId, role);
        }
        return new Seller(signInId, role);
    }
}

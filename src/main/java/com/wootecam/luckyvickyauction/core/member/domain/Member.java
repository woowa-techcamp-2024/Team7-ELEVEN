package com.wootecam.luckyvickyauction.core.member.domain;

public abstract class Member {

    private String signInId;
    private Role role;

    public Member(final String signInId, final Role role) {
        this.signInId = signInId;
        this.role = role;
    }

    public static Member createMemberWithRole(final String signInId, final String userRole) {
        Role role = Role.find(userRole);
        if (role.equals(Role.BUYER)) {
            return new Buyer(signInId, role);
        }
        return new Seller(signInId, role);
    }
}

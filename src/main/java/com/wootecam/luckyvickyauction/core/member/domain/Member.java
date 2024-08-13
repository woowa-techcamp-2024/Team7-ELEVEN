package com.wootecam.luckyvickyauction.core.member.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {

    private Long id;
    private String signInId;
    private String password;
    private Role role;
    protected Point point;

    @Builder
    public Member(String signInId, String password, Role role, Point point) {
        this.signInId = signInId;
        this.password = password;
        this.role = role;
        this.point = point;
    }

    public static Member createMemberWithRole(String signInId, String password, String userRole) {
        Role role = Role.find(userRole);

        return new Member(signInId, password, role, new Point(0L));
    }

    public void usePoint(long price) {
        point.minus(price);
    }

    public void chargePoint(long price) {
        point.plus(price);
    }

    public boolean isBuyer() {
        return role.equals(Role.BUYER);
    }

    public boolean confirmPassword(String password) {
        return this.password.equals(password);
    }

    public boolean isSameMember(String signInId) {
        return this.signInId.equals(signInId);
    }
}

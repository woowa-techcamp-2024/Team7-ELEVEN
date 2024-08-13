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
    public Member(Long id, String signInId, String password, Role role, Point point) {
        this.id = id;
        this.signInId = signInId;
        this.password = password;
        this.role = role;
        this.point = point;
    }

    public static Member createMemberWithRole(String signInId, String password, String userRole) {
        Role role = Role.find(userRole);

        return Member.builder()
                .signInId(signInId)
                .password(password)
                .role(role)
                .point(new Point(0))
                .build();
    }

    public void usePoint(long price) {
        point.minus(price);
    }

    public void chargePoint(long price) {
        point.plus(price);
    }

    public boolean confirmPassword(String password) {
        return this.password.equals(password);
    }

    public boolean isBuyer() {
        return role.equals(Role.BUYER);
    }
}

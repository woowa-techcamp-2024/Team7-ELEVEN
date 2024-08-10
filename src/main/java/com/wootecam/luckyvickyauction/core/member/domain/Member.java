package com.wootecam.luckyvickyauction.core.member.domain;

public abstract class Member {

    private String signInId;
    private Role role;

    public Member(final String signInId, final Role role) {
        this.signInId = signInId;
        this.role = role;
    }
}

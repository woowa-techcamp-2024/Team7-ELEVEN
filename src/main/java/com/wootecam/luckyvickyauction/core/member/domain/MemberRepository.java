package com.wootecam.luckyvickyauction.core.member.domain;

public interface MemberRepository {
    boolean isExist(String signInId);

    void save(String signInId);
}

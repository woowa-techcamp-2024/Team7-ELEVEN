package com.wootecam.luckyvickyauction.core.member.infra;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import java.util.Optional;

public class MemberCoreRepository implements MemberRepository {
    @Override
    public boolean isExist(String signInId) {
        return false;
    }

    @Override
    public Member save(Member member) {
        return null;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Member> findBySignInId(String signInId) {
        return Optional.empty();
    }
}

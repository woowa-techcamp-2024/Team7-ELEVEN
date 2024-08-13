package com.wootecam.luckyvickyauction.core.member.repository;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeMemberRepository implements MemberRepository {

    private Map<Long, Member> members = new HashMap<>();
    private long id = 1L;

    @Override
    public boolean isExist(String signInId) {
        return members.values()
                .stream()
                .anyMatch(member -> member.getSignInId().equals(signInId));
    }

    @Override
    public Member save(Member member) {
        long currentId = id;
        Member savedMember = Member.builder()
                .id(currentId)
                .signInId(member.getSignInId())
                .password(member.getPassword())
                .role(member.getRole())
                .point(member.getPoint())
                .build();

        members.put(currentId, savedMember);
        id++;

        return savedMember;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override
    public Optional<Member> findBySignInId(String signInId) {
        return members.values()
                .stream()
                .filter(member -> member.getSignInId().equals(signInId))
                .findFirst();
    }
}

package com.wootecam.luckyvickyauction.core.member.infra;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        if (Objects.isNull(member.getId())) {
            Member newMember = createNewMember(member);
            members.put(newMember.getId(), newMember);

            return newMember;
        }
        Member existsMember = createExistsMember(member);
        members.put(existsMember.getId(), existsMember);

        return existsMember;
    }

    private Member createNewMember(Member member) {
        return Member.builder()
                .id(id++)
                .signInId(member.getSignInId())
                .password(member.getPassword())
                .role(member.getRole())
                .point(member.getPoint())
                .build();
    }

    private Member createExistsMember(Member member) {
        return Member.builder()
                .id(member.getId())
                .signInId(member.getSignInId())
                .password(member.getPassword())
                .role(member.getRole())
                .point(member.getPoint())
                .build();
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

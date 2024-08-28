package com.wootecam.luckyvickyauction.infra.repository.member;

import com.wootecam.luckyvickyauction.domain.entity.Member;
import com.wootecam.luckyvickyauction.domain.repository.MemberRepository;
import com.wootecam.luckyvickyauction.infra.entity.member.MemberEntity;
import com.wootecam.luckyvickyauction.util.Mapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberCoreRepository implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public boolean isExist(String signInId) {
        return memberJpaRepository.existsBySignInId(signInId);
    }

    @Override
    public Member save(Member member) {
        MemberEntity entity = Mapper.convertToMemberEntity(member);
        MemberEntity saved = memberJpaRepository.save(entity);
        return Mapper.convertToMember(saved);
    }

    @Override
    public Optional<Member> findById(Long id) {
        Optional<MemberEntity> found = memberJpaRepository.findById(id);
        return found.map(Mapper::convertToMember);
    }

    @Override
    public Optional<Member> findBySignInId(String signInId) {
        Optional<MemberEntity> found = memberJpaRepository.findBySignInId(signInId);
        return found.map(Mapper::convertToMember);
    }
}

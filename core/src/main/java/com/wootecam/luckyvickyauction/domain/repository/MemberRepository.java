package com.wootecam.luckyvickyauction.domain.repository;

import com.wootecam.luckyvickyauction.domain.entity.Member;
import java.util.Optional;

public interface MemberRepository {

    boolean isExist(String signInId);

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findBySignInId(String signInId);
}

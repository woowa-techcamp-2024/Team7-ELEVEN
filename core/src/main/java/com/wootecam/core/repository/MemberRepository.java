package com.wootecam.core.repository;

import com.wootecam.core.domain.Member;
import java.util.Optional;

public interface MemberRepository {

    boolean isExist(String signInId);

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findBySignInId(String signInId);
}

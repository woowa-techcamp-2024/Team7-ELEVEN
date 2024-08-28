package com.wootecam.luckyvickyauction.infra.repository.member;

import com.wootecam.luckyvickyauction.infra.entity.member.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findBySignInId(String signInId);

    boolean existsBySignInId(String signInId);
}

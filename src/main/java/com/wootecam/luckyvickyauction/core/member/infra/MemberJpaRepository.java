package com.wootecam.luckyvickyauction.core.member.infra;

import com.wootecam.luckyvickyauction.core.member.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findBySignInId(String signInId);

    boolean existsBySignInId(String signInId);
}

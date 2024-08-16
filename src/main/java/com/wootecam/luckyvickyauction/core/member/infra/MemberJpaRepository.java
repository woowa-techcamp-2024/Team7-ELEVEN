package com.wootecam.luckyvickyauction.core.member.infra;

import com.wootecam.luckyvickyauction.core.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
}

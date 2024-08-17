package com.wootecam.luckyvickyauction;

import com.wootecam.luckyvickyauction.core.member.infra.MemberCoreRepository;
import com.wootecam.luckyvickyauction.global.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({JpaConfig.class, MemberCoreRepository.class})
@DataJpaTest
public class RepositoryTest {
}

package com.wootecam.luckyvickyauction.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.domain.entity.Member;
import com.wootecam.luckyvickyauction.domain.entity.Point;
import com.wootecam.luckyvickyauction.domain.entity.type.Role;
import com.wootecam.luckyvickyauction.infra.entity.member.MemberEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Mapper. Member 관련 테스트
 * <a href="{https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/101}">#101</a>
 */
abstract class MapperMemberTest {
    @Nested
    class 회원_영속성_엔티티를 {
        @Test
        void 도메인_엔티티로_변환하면_정보가_동일하다() {
            // given
            MemberEntity entity = MemberEntity.builder()
                    .id(1L)
                    .signInId("helloworld")
                    .password("password1234")
                    .role(Role.BUYER)
                    .point(12345L)
                    .build();

            // when
            Member member = Mapper.convertToMember(entity);

            // then
            assertAll(
                    () -> assertThat(member.getId()).isEqualTo(1L),
                    () -> assertThat(member.getSignInId()).isEqualTo("helloworld"),
                    () -> assertThat(member.getPassword()).isEqualTo("password1234"),
                    () -> assertThat(member.getRole()).isEqualTo(Role.BUYER),
                    () -> assertThat(member.getPoint().getAmount()).isEqualTo(12345L)
            );
        }
    }

    @Nested
    class 회원_도메인_엔티티를 {
        @Test
        void 영속성_엔티티로_변환하면_정보가_동일하다() {
            // given
            Member member = Member.builder()
                    .id(1L)
                    .signInId("helloworld")
                    .password("password1234")
                    .role(Role.BUYER)
                    .point(new Point(12345))
                    .build();

            // when
            MemberEntity entity = Mapper.convertToMemberEntity(member);

            // then
            assertAll(
                    () -> assertThat(entity.getId()).isEqualTo(1L),
                    () -> assertThat(entity.getSignInId()).isEqualTo("helloworld"),
                    () -> assertThat(entity.getPassword()).isEqualTo("password1234"),
                    () -> assertThat(entity.getRole()).isEqualTo(Role.BUYER),
                    () -> assertThat(entity.getPoint()).isEqualTo(12345L)
            );
        }
    }
}

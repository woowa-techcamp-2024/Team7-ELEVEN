package com.wootecam.core.infra.repository.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.wootecam.core.domain.entity.Member;
import com.wootecam.core.domain.entity.Point;
import com.wootecam.core.domain.entity.type.Role;
import com.wootecam.core.domain.repository.MemberRepository;
import com.wootecam.test.context.RepositoryTest;
import com.wootecam.test.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberCoreRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    class 회원_저장_작업을_수행할_때 {

        @Test
        void 회원_도메인_엔티티를_받으면_정상적으로_저장한다() {
            // given
            Member buyer = Member.builder()
                    .signInId("buyer")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(1000L))
                    .build();

            // when
            Member saved = memberRepository.save(buyer);

            // then
            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isNotNull();  // ID가 자동 생성되었는지 확인
            assertThat(saved.getSignInId()).isEqualTo(buyer.getSignInId());
            assertThat(saved.getPoint()).isEqualTo(buyer.getPoint());
        }
    }

    @Nested
    class 회원_조회_작업을_수행할_때 {

        @Test
        void 저장된_회원이면_정상적으로_반환한다() {
            // given
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();
            Member saved = memberRepository.save(buyer);

            // when
            Optional<Member> found = memberRepository.findById(saved.getId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(saved.getId());
            assertThat(found.get().getSignInId()).isEqualTo(saved.getSignInId());
        }

        @Test
        void 저장되지_않은_회원이면_Optional_empty를_반환한다() {
            // when
            Optional<Member> found = memberRepository.findById(1234567890L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    class 회원_조회_작업을_회원아이디_기준으로_수행할_때 {

        @Test
        void 저장된_회원이면_정상적으로_반환한다() {
            // given
            String signInId = "findthisid";
            Member buyer = Member.builder()
                    .id(1L)
                    .signInId(signInId)
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(1000L))
                    .build();
            memberRepository.save(buyer);

            // when
            Optional<Member> found = memberRepository.findBySignInId(signInId);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getSignInId()).isEqualTo("findthisid");
        }

        @Test
        void 저장되지_않은_회원이면_Optional_empty를_반환한다() {
            // when
            Optional<Member> found = memberRepository.findBySignInId("nonexistent");

            // then
            assertThat(found).isEmpty();
        }
    }
}
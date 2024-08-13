package com.wootecam.luckyvickyauction.core.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.dto.SignInRequestInfo;
import com.wootecam.luckyvickyauction.core.member.dto.SignUpRequestInfo;
import com.wootecam.luckyvickyauction.core.member.repository.FakeMemberRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemberServiceTest {

    private MemberRepository memberRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @Nested
    class signUp_메소드는 {

        @Nested
        class 만약_이미_존재하는_아이디로_회원가입을_시도하면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member member = Member.builder()
                        .signInId("testId")
                        .password("password")
                        .role(Role.BUYER)
                        .point(new Point(0))
                        .build();
                memberRepository.save(member);

                // expect
                assertThatThrownBy(() -> memberService.signUp(new SignUpRequestInfo("testId", "password", "BUYER")))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("이미 존재하는 아이디입니다. input=testId")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.M000));
            }
        }

        @Nested
        class 정상적인_회원가입_요청이_입력되면 {

            @Test
            void 회원가입이_완료된다() {
                // given
                SignUpRequestInfo signUpRequestInfo = new SignUpRequestInfo("testId", "password", "BUYER");

                // when
                memberService.signUp(signUpRequestInfo);
                Member member = memberRepository.findBySignInId("testId").get();

                // then
                assertAll(
                        () -> assertThat(member.getSignInId()).isEqualTo("testId"),
                        () -> assertThat(member.getPassword()).isEqualTo("password"),
                        () -> assertThat(member.getRole()).isEqualTo(Role.BUYER)
                );
            }
        }
    }

    @Nested
    class signIn_메소드는 {

        @Nested
        class 만약_로그인_시_입력한_아이디에_해당되는_사용자를_찾을_수_없으면 {

            @Test
            void 예외가_발생한다() {
                // expect
                assertThatThrownBy(() -> memberService.signIn(new SignInRequestInfo("testId", "password")))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("아이디에 해당되는 사용자를 찾을 수 없습니다. signInId=testId")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.M002));
            }
        }

        @Nested
        class 만약_입력한_패스워드와_실제_패스워드가_일치하지_않는다면 {

            @Test
            void 예외가_발생한다() {
                // given
                Member member = Member.builder()
                        .signInId("testId")
                        .password("password")
                        .role(Role.BUYER)
                        .point(new Point(0))
                        .build();
                memberRepository.save(member);

                // expect
                assertThatThrownBy(() -> memberService.signIn(new SignInRequestInfo("testId", "password1")))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("패스워드가 일치하지 않습니다.")
                        .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                                ErrorCode.M003));
            }
        }

        @Nested
        class 만약_정상적인_로그인_요청이라면 {

            @Test
            void 로그인한_사용자의_로그인에_성공한_사용자의_식별자와_역할을_반환한다() {
                // given
                Member member = Member.builder()
                        .signInId("testId")
                        .password("password")
                        .role(Role.BUYER)
                        .point(new Point(0))
                        .build();
                memberRepository.save(member);

                // when
                SignInInfo signInInfo = memberService.signIn(new SignInRequestInfo("testId", "password"));

                // then
                assertAll(
                        () -> assertThat(signInInfo.id()).isEqualTo(1L),
                        () -> assertThat(signInInfo.role()).isEqualTo(Role.BUYER)
                );
            }
        }
    }
}

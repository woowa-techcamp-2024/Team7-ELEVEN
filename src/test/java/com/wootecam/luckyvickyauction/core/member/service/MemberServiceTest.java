package com.wootecam.luckyvickyauction.core.member.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// TODO: Spring 의존성, DB 뭍으면 테스트 작성하기
class MemberServiceTest {

    @Nested
    class signUp_메소드는 {

        @Nested
        class 만약_이미_존재하는_아이디로_회원가입을_시도하면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 정상적인_회원가입_요청이_입력되면 {

            @Test
            void 회원가입이_완료된다() {
                // given
                // when
                // then
            }
        }
    }
}

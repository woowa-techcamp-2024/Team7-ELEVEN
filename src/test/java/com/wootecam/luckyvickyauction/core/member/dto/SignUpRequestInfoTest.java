package com.wootecam.luckyvickyauction.core.member.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class SignUpRequestInfoTest {

    static Stream<Arguments> generateSignUpRequestInfo() {
        return Stream.of(
                Arguments.of(null, "password1234", "BUYER"),
                Arguments.of("userid1234", null, "BUYER"),
                Arguments.of(null, "password1234", null)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    "})
    void 회원가입요청에서_아이디가_빈칸이면_예외가_발생한다(String userId) {
        assertThatThrownBy(() ->
                new SignUpRequestInfo(userId, "password1234", "SELLER"))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.M004);
    }

    @ParameterizedTest
    @MethodSource("generateSignUpRequestInfo")
    void 회원가입요청에서_어떤값이라도_Null이_입력되는_경우_예외가_발생한다(String userId, String password, String role) {
        // expect
        assertThatThrownBy(() ->
                new SignUpRequestInfo(userId, password, role))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.G000);
    }
}
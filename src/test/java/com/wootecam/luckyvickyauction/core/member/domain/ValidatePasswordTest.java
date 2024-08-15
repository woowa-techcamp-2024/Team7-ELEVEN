package com.wootecam.luckyvickyauction.core.member.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/104">#104</a>
 */
public class ValidatePasswordTest {

    @ParameterizedTest
    @MethodSource("generateInvalidPassword")
    void 유효하지_않은_비밀번호면_예외가_발생한다(String expectedMessage, String password, ErrorCode expectedErrorCode) {
        // expect
        assertThatThrownBy(() -> Member.builder()
                .signInId("testId")
                .password(password)
                .role(Role.BUYER)
                .point(new Point(100))
                .build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage(expectedMessage)
                .hasFieldOrPropertyWithValue("errorCode", expectedErrorCode);
    }

    static Stream<Arguments> generateInvalidPassword() {
        return Stream.of(
                Arguments.of("비밀번호는 8자 이상 20자 이하로 입력해주세요.", "p00", ErrorCode.M006),
                Arguments.of("비밀번호는 8자 이상 20자 이하로 입력해주세요.", "passwordpasswordpassword00", ErrorCode.M006),
                Arguments.of("비밀번호는 숫자가 반드시 포함되어야 합니다.", "password", ErrorCode.M007),
                Arguments.of("비밀번호는 알파벳 소문자가 반드시 포함되어야 합니다.", "PASSWORD00", ErrorCode.M008)
        );
    }
}

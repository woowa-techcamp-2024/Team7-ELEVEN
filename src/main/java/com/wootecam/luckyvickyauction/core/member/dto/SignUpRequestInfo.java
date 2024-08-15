package com.wootecam.luckyvickyauction.core.member.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.util.Objects;

public record SignUpRequestInfo(
        String signUpId,
        String password,
        String userRole
) {

    private static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";
    private static final String ERROR_ID_IS_BLANK = "아이디는 빈칸 또는 공백일 수 없습니다.";

    public SignUpRequestInfo {
        validateNotNull(signUpId, "회원가입 ID");
        validateNotNull(password, "회원가입 패스워드");
        validateNotNull(userRole, "사용자 역할");

        validateSignUpId(signUpId);
        validatePassword(password);
    }

    private void validateSignUpId(String signUpId) {
        if (signUpId.isBlank()) {
            throw new BadRequestException(ERROR_ID_IS_BLANK, ErrorCode.M004);
        }
    }

    // TODO: [패스워드 제약사항 이슈 작업 시 추가] [writeAt: 2024/08/15/19:09] [writeBy: chhs2131]
    private void validatePassword(String password) {
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }
}

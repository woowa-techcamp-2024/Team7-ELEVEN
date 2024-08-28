package com.wootecam.luckyvickyauction.dto.member.info;

import com.wootecam.luckyvickyauction.exception.BadRequestException;
import com.wootecam.luckyvickyauction.exception.ErrorCode;
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
    }

    private void validateSignUpId(String signUpId) {
        if (signUpId.isBlank()) {
            throw new BadRequestException(ERROR_ID_IS_BLANK, ErrorCode.M004);
        }
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }
}

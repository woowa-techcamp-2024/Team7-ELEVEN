package com.wootecam.luckyvickyauction.core.member.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.util.Objects;

public record SignUpInfo(
        String signUpId,
        String password,
        String userRole
) {

    private static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public SignUpInfo {
        validateNotNull(signUpId, "회원가입 ID");
        validateNotNull(password, "회원가입 패스워드");
        validateNotNull(userRole, "사용자 역할");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.A007);
        }
    }
}

package com.wootecam.luckyvickyauction.core.member.dto;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.util.Objects;

public record SignInRequestInfo(
        String signInId,
        String password
) {

    private static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public SignInRequestInfo {
        validateNotNull(signInId, "로그인 ID");
        validateNotNull(password, "로그인 패스워드");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }
}

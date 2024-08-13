package com.wootecam.luckyvickyauction.core.member.dto;

import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.util.Objects;

public record SignInInfo(
        Long id,
        Role role
) {
    private static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";

    public SignInInfo {
        validateNotNull(id, "로그인한 사용자의 식별자");
        validateNotNull(role, "로그인한 사용자의 역할");
    }

    private void validateNotNull(Object value, String fieldName) {
        if (Objects.isNull(value)) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }

    public boolean isType(Role role) {
        return this.role.equals(role);
    }
}

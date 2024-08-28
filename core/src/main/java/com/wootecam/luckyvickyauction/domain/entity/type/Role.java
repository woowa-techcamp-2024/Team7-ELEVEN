package com.wootecam.luckyvickyauction.domain.entity.type;

import static com.wootecam.luckyvickyauction.exception.ErrorCode.M001;

import com.wootecam.luckyvickyauction.exception.BadRequestException;
import java.util.Arrays;

public enum Role {

    SELLER("판매자"),
    BUYER("입찰자(구매자");

    private final String description;

    Role(final String description) {
        this.description = description;
    }

    public static Role find(final String userRole) {
        return Arrays.stream(values())
                .filter(role -> role.name().equals(userRole))
                .findAny()
                .orElseThrow(() -> new BadRequestException("사용자의 역할을 찾을 수 없습니다. userRole = " + userRole, M001));
    }
}

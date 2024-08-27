package com.wootecam.core.dto.member.info;


import com.wootecam.core.domain.entity.type.Role;

public record SignInResponseInfo(
        Role role
) {
}

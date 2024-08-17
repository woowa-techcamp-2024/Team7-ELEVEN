package com.wootecam.luckyvickyauction.core.member.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank(message = "아이디는 반드시 입력해야 합니다.")
        String signInId,

        @NotBlank(message = "패스워드는 반드시 입력해야 합니다.")
        String password
) {
}

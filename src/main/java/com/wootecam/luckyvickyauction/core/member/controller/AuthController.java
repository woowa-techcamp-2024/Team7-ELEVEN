package com.wootecam.luckyvickyauction.core.member.controller;

import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.dto.SignInRequestInfo;
import com.wootecam.luckyvickyauction.core.member.dto.SignUpRequestInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

// @RestController  // TODO: [선행 @Repository가 생길 때, 주석을 풀 것] [writeAt: 2024/08/16/16:12] [writeBy: chhs2131]
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // 신규 회원은 회원가입을 한다.
    @PostMapping("/signup")
    public void signup(@RequestBody SignUpRequestInfo request) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

    // 사용자는 로그인을 한다.
    @PostMapping("/signin")
    public SignInInfo signin(@RequestBody SignInRequestInfo request) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }
}

package com.wootecam.luckyvickyauction.core.member.controller;

import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.dto.SignInRequestInfo;
import com.wootecam.luckyvickyauction.core.member.dto.SignUpRequestInfo;
import com.wootecam.luckyvickyauction.core.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // 신규 회원은 회원가입을 한다.
    @PostMapping("/signup")
    public void signup(@RequestBody SignUpRequestInfo request) {
        memberService.signUp(request);
    }

    // 사용자는 로그인을 한다.
    @PostMapping("/signin")
    public SignInInfo signin(@RequestBody SignInRequestInfo request) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }
}

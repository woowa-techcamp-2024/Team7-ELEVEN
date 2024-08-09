package com.wootecam.luckyvickyauction.core.member.service;

import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void signIn(String signInId, HttpSession session) {
        if (memberRepository.isExist(signInId)) {
            throw new BadRequestException("이미 존재하는 아이디입니다. input=" + signInId, ErrorCode.M000);
        }

        memberRepository.save(signInId);
        session.setAttribute("signInId", signInId);
    }

    public void signOut(HttpSession session) {
        session.invalidate();
    }
}

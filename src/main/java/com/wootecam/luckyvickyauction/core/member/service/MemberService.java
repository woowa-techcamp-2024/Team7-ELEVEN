package com.wootecam.luckyvickyauction.core.member.service;

import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void signIn(String signInId, HttpSession session) {
        if (memberRepository.isExist(signInId)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        memberRepository.save(signInId);
        session.setAttribute("signInId", signInId);
    }

    public void signOut(HttpSession session) {
        session.invalidate();
    }
}

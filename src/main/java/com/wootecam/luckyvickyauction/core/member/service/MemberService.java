package com.wootecam.luckyvickyauction.core.member.service;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 로그인(회원가입)을 동시에 처리한다.
     * @param signInId 로그인(회원가입)할 아이디
     * @param userRole 사용자의 역할(구매자, 판매자)
     */
    public void signIn(String signInId, String userRole, HttpSession session) {
        if (memberRepository.isExist(signInId)) {
            throw new BadRequestException("이미 존재하는 아이디입니다. input=" + signInId, ErrorCode.M000);
        }
        Member member = Member.createMemberWithRole(signInId, userRole);
        Member savedMember = memberRepository.save(member);

        session.setAttribute("signInUser", savedMember);
    }

    public void signOut(HttpSession session) {
        session.invalidate();
    }
}

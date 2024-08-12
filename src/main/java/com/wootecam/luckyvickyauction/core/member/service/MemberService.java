package com.wootecam.luckyvickyauction.core.member.service;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.MemberRepository;
import com.wootecam.luckyvickyauction.core.member.dto.SignUpInfo;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void signUp(SignUpInfo signUpInfo) {
        if (memberRepository.isExist(signUpInfo.signUpId())) {
            throw new BadRequestException("이미 존재하는 아이디입니다. input=" + signUpInfo.signUpId(), ErrorCode.M000);
        }
        Member member = Member.createMemberWithRole(
                signUpInfo.signUpId(),
                signUpInfo.password(),
                signUpInfo.userRole()
        );

        memberRepository.save(member);
    }

    public void signOut(HttpSession session) {
        session.invalidate();
    }
}

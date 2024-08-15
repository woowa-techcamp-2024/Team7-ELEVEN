package com.wootecam.luckyvickyauction.core.member.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {

    private Long id;
    private String signInId;
    private String password;
    private Role role;
    protected Point point;

    private static final int MINIMUM_ID_LENGTH = 2;
    private static final int MAXIMUM_ID_LENGTH = 20;
    private static final String ERROR_ID_IS_BLANK = "아이디는 빈칸 또는 공백일 수 없습니다.";
    private static final String ERROR_SIGN_IN_ID_LENGTH = "아이디 길이는 %d ~ %d자 여야합니다. 현재 길이: %d";

    @Builder
    public Member(Long id, String signInId, String password, Role role, Point point) {
        validateSignInId(signInId);

        this.id = id;
        this.signInId = signInId;
        this.password = password;
        this.role = role;
        this.point = point;

        validatePassword(password);
    }

    /**
     * 비밀번호 유효성 검사 <br>
     * 1. 비밀번호는 최소 8자, 최대 20자리이다. <br>
     * 2. 비밀번호는 숫자가 반드시 포함되어야한다. <br>
     * 3. 비밀번호는 알파벳 소문자가 반드시 포함되어야한다. <br>
     * @param password 비밀번호
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            throw new BadRequestException("비밀번호는 8자 이상 20자 이하로 입력해주세요.", ErrorCode.M006);
        }

        if (!password.matches(".*[0-9].*")) {
            throw new BadRequestException("비밀번호는 숫자가 반드시 포함되어야 합니다.", ErrorCode.M007);
        }

        if (!password.matches(".*[a-z].*")) {
            throw new BadRequestException("비밀번호는 알파벳 소문자가 반드시 포함되어야 합니다.", ErrorCode.M008);
        }
    }

    private void validateSignInId(String signInId) {
        // validate not blank
        if (signInId == null || signInId.isBlank()) {
            throw new BadRequestException(ERROR_ID_IS_BLANK, ErrorCode.M004);
        }
        // validate length
        if (signInId.length() < MINIMUM_ID_LENGTH || MAXIMUM_ID_LENGTH < signInId.length()) {
            throw new BadRequestException(
                    String.format(ERROR_SIGN_IN_ID_LENGTH, MINIMUM_ID_LENGTH, MAXIMUM_ID_LENGTH, signInId.length()),
                    ErrorCode.M005);
        }
    }

    public static Member createMemberWithRole(String signInId, String password, String userRole) {
        Role role = Role.find(userRole);

        return Member.builder()
                .signInId(signInId)
                .password(password)
                .role(role)
                .point(new Point(0))
                .build();
    }

    public void usePoint(long price) {
        point.minus(price);
    }

    public void chargePoint(long price) {
        point.plus(price);
    }

    public boolean confirmPassword(String password) {
        return this.password.equals(password);
    }

    public boolean isSameMember(String signInId) {
        return this.signInId.equals(signInId);
    }

    public boolean isBuyer() {
        return role.equals(Role.BUYER);
    }
}

package com.wootecam.luckyvickyauction.global.exception;

public enum ErrorCode {

    // Member 관련 예외 코드
    M000("로그인(회원가입) 시, 이미 존재하는 회원 아이디로 로그인을 시도한 경우 예외가 발생합니다."),
    M001("로그인(회원가입) 시, 사용자의 역할(구매자, 판매자)를 찾을 수 없는 경우 예외가 발생합니다."),

    // Payment 관련 예외 코드
    P000("입찰 시, 로그인한 사용자가 구매자가 아닌 경우 예외가 발생합니다."),
    P001("입찰 시, 남아있는 재고보다 더 많은 입찰을 시도한 경우 예외가 발생합니다."),
    P002("입찰 시, 사용자의 포인트가 부족한 경우 예외가 발생합니다.");

    private final String description;

    ErrorCode(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

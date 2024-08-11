package com.wootecam.luckyvickyauction.global.exception;

public enum ErrorCode {
    // Auction
    // TODO 어느 시점에 발생하는 에러d인지 명시
    A000("경매 재고는 인당 구매 수량보다 작을 수 없습니다."),
    A001("상품 이름은 비어있을 수 없습니다."),
    A002("상품 원가는 0과 같거나 작을 수 없습니다."),
    A003("최대 구매 수량 제한은 0과 같거나 작을 수 없습니다."),
    A004("가격 변동폭은 0과 같거나 작을 수 없습니다."),
    A005("변동 시간 단위는 0과 같거나 작을 수 없습니다."),
    A006("경매의 시작 시간은 종료 시간보다 클 수 없습니다."),
    A007("필수 값을 모두 입력해야됩니다."),
    A008("경매 지속시간은 10, 20, 30, 40, 50, 60분이어야 합니다."),
    A009("경매 가격 변동폭은 경매 가격보다 낮아야 합니다."),
    A010("할인율은 0 초과 100 미만이어야 합니다."),

    // Member 관련 예외 코드
    M000("로그인(회원가입) 시, 이미 존재하는 회원 아이디로 로그인을 시도한 경우 예외가 발생합니다."),
    M001("로그인(회원가입) 시, 사용자의 역할(구매자, 판매자)를 찾을 수 없는 경우 예외가 발생합니다."),

    // Payment 관련 예외 코드
    P000("입찰 시, 로그인한 사용자가 구매자가 아닌 경우 예외가 발생합니다."),
    P001("입찰 시, 사용자의 포인트가 부족한 경우 예외가 발생합니다.");

    private final String description;

    ErrorCode(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

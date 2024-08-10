package com.wootecam.luckyvickyauction.global.exception;

public enum ErrorCode {
    // Auction
    A000("필수 값을 모두 입력해야됩니다."),
    A001("상품 이름은 비어있을 수 없습니다."),
    A002("상품 원가는 0과 같거나 작을 수 없습니다."),
    A003("최대 구매 수량 제한은 0과 같거나 작을 수 없습니다."),
    A004("가격 변동폭은 0과 같거나 작을 수 없습니다."),
    A005("변동 시간 단위는 0과 같거나 작을 수 없습니다."),
    A006("경매의 시작 시간은 종료 시간보다 클 수 없습니다."),
    A007("경매 재고는 인당 구매 수량보다 작을 수 없습니다."),
    A008("경매 지속시간은 10, 20, 30, 40, 50, 60분이어야 합니다.");

    private final String description;

    ErrorCode(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

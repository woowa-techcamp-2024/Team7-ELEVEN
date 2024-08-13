package com.wootecam.luckyvickyauction.global.exception;

public enum ErrorCode {
    // Auction
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
    A011("경매ID를 기준으로 경매를 찾으려고 했지만 찾을 수 없습니다."),
    A012("이미 시작된 경매를 변경하려고 할 때, 예외가 발생합니다."),
    A013("경매 정보 생성 시, 현재 가격은 0과 같거나 작을 경우 예외가 발생합니다."),
    A014("요청 수량만큼 경매 상품 구입이 불가능 할 때 예외가 발생합니다."),
    A015("해당 판매자는 수정할 권한이 없습니다."),
    A016("진행 중이지 않은 경매를 입찰하려고 할 때, 예외가 발생합니다."),
    A017("경매 재고 변경 시, 요청한 사용자가 판매자가 아닐 경우 예외가 발생합니다."),
    A018("경매 재고 변경 시, 경매를 생성한 판매자와 재고 변경 요청 사용자가 다를 경우 예외가 발생합니다."),
    A019("경매 재고 변경 시, 경매 재고를 1개 미만으로 수정하려 했을때 예외가 발생합니다."),

    // BidHistory 관련 예외 코드
    B000("거래 내역 조회 시, 입찰 내역을 찾을 수 없을 경우 예외가 발생합니다."),
    B001("입찰 내역 조회 시, 사용자가 해당 거래의 구매자 또는 판매자가 아닌 경우 예외가 발생합니다."),

    // Member 관련 예외 코드
    M000("로그인(회원가입) 시, 이미 존재하는 회원 아이디로 로그인을 시도한 경우 예외가 발생합니다."),
    M001("로그인(회원가입) 시, 사용자의 역할(구매자, 판매자)를 찾을 수 없는 경우 예외가 발생합니다."),
    M002("사용자 조회 시, 사용자를 찾을 수 없는 경우 예외가 발생합니다."),
    M003("로그인 시, 입력 패스워드와 실제 패스워드가 다른 경우 예외가 발생합니다."),

    // Payment 관련 예외 코드
    P000("거래 시, 로그인한 사용자가 구매자가 아닌 경우 예외가 발생합니다."),
    P001("입찰 시, 사용자의 포인트가 부족한 경우 예외가 발생합니다."),
    P002("거래 내역 조회 시, 입찰 내역을 찾을 수 없을 경우 예외가 발생합니다."),
    P003("환불 시, 이미 환불된 입찰 내역일 경우 예외가 발생합니다."),
    P004("환불 시, 요청한 사용자가 환불할 입찰의 구매자가 아닌 경우 예외가 발생합니다."),

    // Global 예외
    G000("DTO 생성 시, 필드의 값이 NULL인 경우 예외가 발생합니다.");

    private final String description;

    ErrorCode(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

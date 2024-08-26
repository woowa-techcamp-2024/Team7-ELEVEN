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
    A007("경매 생성 시, 경매 지속시간은 10, 20, 30, 40, 50, 60분이 아닌 경우 예외가 발생합니다."),
    A008("경매 가격 변동폭은 경매 가격보다 낮아야 합니다."),
    A009("경매 생성 시, 할인율이 0퍼센트 미만이거나 50 퍼센트를 초과한 경우 예외가 발생합니다."),
    A010("경매ID를 기준으로 경매를 찾으려고 했지만 찾을 수 없습니다."),
    A011("경매 정보 생성 시, 현재 가격은 0과 같거나 작을 경우 예외가 발생합니다."),
    A012("요청 수량만큼 경매 상품 구입이 불가능 할 때 예외가 발생합니다."),
    A013("진행 중이지 않은 경매를 입찰하려고 할 때, 예외가 발생합니다."),
    A014("경매 생성 시, 경매 시작 시간이 현재 시간보다 이른 경우 예외가 발생합니다."),
    A015("환불로 인한 재고 변경 시, 추가 및 삭제하는 재고량이 1 미만일 경우 예외가 발생합니다."),
    A016("환불로 인한 재고 변경 시, 변경 후의 재고가 원래 재고보다 많을 경우 예외가 발생합니다."),
    A017("경매 취소 시, 사용자 역할이 판매자가 아닌 경우 예외가 발생합니다."),
    A018("경매 취소 시, 경매를 생성한 판매자와 경매 취소 요청 사용자가 다를 경우 예외가 발생합니다."),
    A019("경매 취소 시, 경매가 준비중인 상태가 아닐때 예외가 발생합니다."),
    A020("경매 상세 조회 시, 요청한 판매자가 경매의 판매자가 아닐 경우 예외가 발생합니다."),
    A021("경매 생성 시, 가격 변동 정책이 적용된 경매에서 최대 할인가를 적용했을떄 최소 가격 이하로 떨어지는 경우 예외가 발생합니다."),
    A022("경매 입찰 진행 시, 현재 상품의 가격과 사용자가 구매 요청한 가격이 다를때 예외가 발생합니다."),
    A023("가격 정책 객체를 JSON으로 변환 시, 변환이 불가능할 경우 예외가 발생합니다."),
    A024("JSON을 가격 정책 객체로 변환 시, 타입이 올바르지 않을 경우 예외가 발생합니다."),
    A025("JSON을 가격 정책 객체로 변환 시, 변환이 불가능할 경우 예외가 발생합니다."),
    A026("경매 입찰 요청 시, 요청 가격이 0보다 작으면 예외가 발생합니다."),
    A027("경매 입찰 요청 시, 요청 수량이 1 미만일 경우 예외가 발생합니다."),
    A028("경매 생성 시, 경매 할인 주기 시간이 1, 5, 10분이 아닌 경우 예외가 발생합니다."),
    A029("경매 생성 시, 경매 할인 주기 시간이 경매 지속 시간보다 크거나 같은 경우 예외가 발생합니다."),
    A030("경매 환불 시, 환불할 경매를 조회할 수 없는 경우 예외가 발생합니다."),

    // Receipt 관련 예외 코드
    R000("거래 내역 조회 시, 거래 내역을 찾을 수 없을 경우 예외가 발생합니다."),
    R001("거래 내역 조회 시, 사용자가 해당 거래의 구매자 또는 판매자가 아닌 경우 예외가 발생합니다."),
    R002("환불 시, 이미 환불된 거래 내역일 경우 예외가 발생합니다."),
    R003("경매 환불 시, 환불할 거래 내역을 조회할 수 없는 경우 예외가 발생합니다."),

    // Member 관련 예외 코드
    M000("로그인(회원가입) 시, 이미 존재하는 회원 아이디로 로그인을 시도한 경우 예외가 발생합니다."),
    M001("로그인(회원가입) 시, 사용자의 역할(구매자, 판매자)를 찾을 수 없는 경우 예외가 발생합니다."),
    M002("사용자 조회 시, 사용자를 찾을 수 없는 경우 예외가 발생합니다."),
    M003("로그인 시, 입력 패스워드와 실제 패스워드가 다른 경우 예외가 발생합니다."),
    M004("회원가입 시, 사용자 아이디가 비어있는 경우 예외가 발생합니다."),
    M005("회원가입 시, 사용자 아이디가 글자수 제한 정책에 맞지 않으면 예외가 발생합니다."),
    M006("회원가입 시, 비밀번호가 빈칸 또는 공백인 경우 예외가 발생합니다."),
    M007("회원가입 시, 비밀번호는 8자 이상 20자 이하가 아닌 경우 예외가 발생합니다."),
    M008("회원가입 시, 비밀번호에 숫자가 포함되어 있지 않은 경우 예외가 발생합니다."),
    M009("회원가입 시, 비밀번호에 알파벳 소문자가 포함되어 있지 않은 경우 예외가 발생합니다."),
    M010("회원가입 시, 비밀번호에 영문자와 숫자 외에 다른 문자가 포함되어 있는 경우 예외가 발생합니다."),

    // Payment 관련 예외 코드
    P000("구매 시, 로그인한 사용자가 구매자가 아닌 경우 예외가 발생합니다."),
    P001("구매 시, 사용자의 포인트가 부족한 경우 예외가 발생합니다."),
    P002("거래 내역 조회 시, 거래 내역을 찾을 수 없을 경우 예외가 발생합니다."),
    P004("환불 시, 요청한 사용자가 환불할 거래의 구매자가 아닌 경우 예외가 발생합니다."),
    P005("포인트 충전 시, 충전할 포인트가 0보다 작을 경우 예외가 발생합니다."),
    P006("포인트 충전 시, 충전 후 포인트가 Long 최대값을 초과할 경우 예외가 발생합니다."),
    P007("환불 요청 시, 종료된 경매에 대한 환불이 아닌 경우 예외가 발생합니다."),
    P008("포이늩 연산 시, 음수 값을 연산에 사용하는 경우 예외가 발생합니다."),

    // 인증, 인가 관련 예외 코드
    AU00("API 요청 시, 비로그인 사용자가 허락되지 않은 엔드포인트에 접근 할 경우 예외가 발생합니다."),
    AU01("API 요청 시, 판매자가 아닌 사람이 판매자만 접근할 수 있는 엔드포인트에 접근 할 경우 예외가 발생합니다."),
    AU02("API 요청 시, 구매자가 아닌 사람이 구매자만 접근할 수 있는 엔드포인트에 접근 할 경우 예외가 발생합니다."),
    AU03("API 요청 시, Roles에 명시된 권한을 하나라도 갖지 않은 사용자가 엔드포인트에 접근 할 경우 예외가 발생합니다."),

    // Global 예외
    G000("DTO 생성 시, 필드의 값이 NULL인 경우 예외가 발생합니다."),
    G001("목록 조회시, 과도한 데이터를 조회할 수 없습니다."),
    G002("Lock 획득 시, TimeOut 시간을 초과하면 예외가 발생합니다."),
    G003("Lock 획득 시, 시스템 문제로 락을 획득하지 못한 경우 예외가 발생합니다."),

    // 서버 예외
    SERVER_ERROR("서버에서 예기치 못한 예외가 발생한 경우");

    private final String description;

    ErrorCode(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

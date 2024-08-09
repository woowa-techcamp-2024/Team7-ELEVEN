package com.wootecam.luckyvickyauction.global.exception;

public enum ErrorCode {

    // test 에러 코드
    T000("API문서에서 에러 코드에 대한 정보를 담을때 사용됩니다.");

    private final String description;

    ErrorCode(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

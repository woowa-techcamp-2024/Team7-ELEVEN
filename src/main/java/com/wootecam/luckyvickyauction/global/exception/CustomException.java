package com.wootecam.luckyvickyauction.global.exception;

public class CustomException extends RuntimeException {

    private final String errorCode;

    public CustomException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

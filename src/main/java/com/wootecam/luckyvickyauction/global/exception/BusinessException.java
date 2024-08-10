package com.wootecam.luckyvickyauction.global.exception;

public class BusinessException extends RuntimeException {

    private final int statusCode;
    private final ErrorCode errorCode;

    public BusinessException(final String message, final int statusCode, final ErrorCode errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

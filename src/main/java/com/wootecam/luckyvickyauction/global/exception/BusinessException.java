package com.wootecam.luckyvickyauction.global.exception;

public class BusinessException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;

    public BusinessException(final String message, final int statusCode, final String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

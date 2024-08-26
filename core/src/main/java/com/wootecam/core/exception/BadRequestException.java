package com.wootecam.core.exception;

public class BadRequestException extends BusinessException {

    public BadRequestException(final String message, final ErrorCode errorCode) {
        super(message, 400, errorCode);
    }
}

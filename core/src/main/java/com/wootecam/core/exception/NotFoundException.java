package com.wootecam.core.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(final String message, final ErrorCode errorCode) {
        super(message, 404, errorCode);
    }
}

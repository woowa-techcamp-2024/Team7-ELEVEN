package com.wootecam.luckyvickyauction.exception;

public class ServiceUnavailableException extends BusinessException {

    public ServiceUnavailableException(final String message, final ErrorCode errorCode) {
        super(message, 503, errorCode);
    }
}

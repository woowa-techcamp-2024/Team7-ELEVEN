package com.wootecam.luckyvickyauction.global.exception;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class ServiceUnavailableException extends BusinessException {

    public ServiceUnavailableException(final String message, final ErrorCode errorCode) {
        super(message, SERVICE_UNAVAILABLE.value(), errorCode);
    }
}

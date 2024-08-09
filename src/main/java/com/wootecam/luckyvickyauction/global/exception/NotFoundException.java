package com.wootecam.luckyvickyauction.global.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class NotFoundException extends BusinessException {

    public NotFoundException(final String message, final ErrorCode errorCode) {
        super(message, NOT_FOUND.value(), errorCode.name());
    }
}

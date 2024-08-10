package com.wootecam.luckyvickyauction.global.exception;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }
}

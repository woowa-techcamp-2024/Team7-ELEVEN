package com.wootecam.luckyvickyauction.exception;

public class AuthenticationException extends CustomException {

    public AuthenticationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}

package com.wootecam.luckyvickyauction.global.exception;

public class InfraStructureException extends CustomException {

    public InfraStructureException(final String message, final ErrorCode errorCode) {
        super(message, errorCode.name());
    }
}

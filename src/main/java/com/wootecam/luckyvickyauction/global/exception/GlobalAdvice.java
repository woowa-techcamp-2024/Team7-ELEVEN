package com.wootecam.luckyvickyauction.global.exception;


import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(final AuthenticationException e) {
        log.warn("ERROR CODE {} : {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(e.getMessage(), e.getErrorCode().name()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorization(final AuthorizationException e) {
        log.warn("ERROR CODE {} : {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(e.getMessage(), e.getErrorCode().name()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(final BusinessException e) {
        log.warn("ERROR CODE {} : {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getStatusCode())
                .body(ErrorResponse.of(e.getMessage(), e.getErrorCode().name()));
    }

    @ExceptionHandler(InfraStructureException.class)
    public ResponseEntity<ErrorResponse> handleInfraStructure(final InfraStructureException e) {
        log.warn("ERROR CODE {} : {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(e.getMessage(), e.getErrorCode().name()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(final Exception e) {
        StringWriter out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        e.printStackTrace(printWriter);

        log.error("Stack Trace : {}", out);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(e.getMessage(), ErrorCode.SERVER_ERROR.name()));
    }
}

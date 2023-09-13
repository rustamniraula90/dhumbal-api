package com.fyp.dhumbal.global.error.exception;

import com.fyp.dhumbal.global.error.model.ErrorResponse;
import com.fyp.dhumbal.global.error.model.ValidationError;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;
import java.util.List;

public abstract class BaseException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public BaseException(String code, String message) {
        super(message);
        this.errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .statusCode(String.valueOf(getHttpStatus().value()))
                .time(Instant.now().toString())
                .build();
    }

    public BaseException(String code, String message, List<ValidationError> errors) {
        super(message);
        this.errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .statusCode(String.valueOf(getHttpStatus().value()))
                .time(Instant.now().toString())
                .errors(errors)
                .build();
    }

    public BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .statusCode(String.valueOf(getHttpStatus().value()))
                .time(Instant.now().toString())
                .build();
    }

    public abstract HttpStatusCode getHttpStatus();

    public void setPath(String path) {
        errorResponse.setPath(path);
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }
}

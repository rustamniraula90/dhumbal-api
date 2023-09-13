package com.fyp.dhumbal.global.error.exception.impl;

import com.fyp.dhumbal.global.error.exception.BaseException;
import com.fyp.dhumbal.global.error.model.ValidationError;
import org.springframework.http.HttpStatus;

import java.util.List;

public class BadRequestException extends BaseException {

    public BadRequestException(String code, String message) {
        super(code, message);
    }

    public BadRequestException(String code, String message, List<ValidationError> errors) {
        super(code, message, errors);
    }


    public BadRequestException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

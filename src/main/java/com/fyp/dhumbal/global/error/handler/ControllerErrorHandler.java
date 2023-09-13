package com.fyp.dhumbal.global.error.handler;

import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.BaseException;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.error.exception.impl.ForbiddenException;
import com.fyp.dhumbal.global.error.exception.impl.InternalServerException;
import com.fyp.dhumbal.global.error.model.ErrorResponse;
import com.fyp.dhumbal.global.validation.BaseValidation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerErrorHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerErrorHandler.class);

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatusCode status,
                                                         WebRequest request) {
        BaseValidation<?> validation = new BaseValidation<>(ex);
        BaseException exception = new BadRequestException(ErrorCodes.BAD_REQUEST,
                "Validation Failed. Please check the data and try again.", validation.getErrors());
        return new ResponseEntity<>(exception.getErrorResponse(), exception.getHttpStatus());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e, HttpServletRequest request) {
        BaseException exception = new ForbiddenException(ErrorCodes.RESOURCE_NOT_FOUND, "The resource doesn't exists or you aren't authorized to access this resource.");
        this.addPathToErrorResponse(exception, request);
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getErrorResponse());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Exception caught on base controller ", e);
        BaseException baseException;
        if (e instanceof BaseException) baseException = (BaseException) e;
        else
            baseException = new InternalServerException(ErrorCodes.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.");
        this.addPathToErrorResponse(baseException, request);
        return ResponseEntity.status(baseException.getHttpStatus()).body(baseException.getErrorResponse());
    }

    private void addPathToErrorResponse(BaseException baseException, HttpServletRequest request) {
        baseException.setPath(request.getRequestURI());
    }
}
package com.fyp.dhumbal.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.BaseException;
import com.fyp.dhumbal.global.error.exception.impl.UnauthorizedRequestException;
import com.fyp.dhumbal.global.error.model.ErrorResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        BaseException exception = new UnauthorizedRequestException(ErrorCodes.UNAUTHORIZED, "Authorization failed or token expired");
        response.setStatus(exception.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = exception.getErrorResponse();
        errorResponse.setPath(request.getRequestURI());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(errorResponse);
        try (ServletOutputStream stream = response.getOutputStream()) {
            stream.print(json);
            stream.flush();
        }
    }
}
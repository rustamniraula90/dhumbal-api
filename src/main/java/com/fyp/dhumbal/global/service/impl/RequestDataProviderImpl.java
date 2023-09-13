package com.fyp.dhumbal.global.service.impl;

import com.fyp.dhumbal.global.config.security.AuthConstant;
import com.fyp.dhumbal.global.service.RequestDataProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RequestDataProviderImpl implements RequestDataProvider {

    private final HttpServletRequest httpServletRequest;

    public static final String FORWARDED_IP_ADDRESS_HEADER = "X-FORWARDED-FOR";
    public static final String USER_AGENT_HEADER = "user-agent";

    @Override
    public String getRequestIp() {
        String ipAddress = httpServletRequest.getHeader(FORWARDED_IP_ADDRESS_HEADER);
        if (ipAddress == null) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        return ipAddress;
    }

    @Override
    public String getRequestUserAgent() {
        return httpServletRequest.getHeader(USER_AGENT_HEADER);
    }

    @Override
    public String getAccessToken() {
        return getHeaderData(AuthConstant.AUTH_HEADER_NAME);
    }

    private String getHeaderData(String name) {
        return httpServletRequest.getHeader(name);
    }
}

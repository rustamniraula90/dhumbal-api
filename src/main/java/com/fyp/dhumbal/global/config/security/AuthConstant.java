package com.fyp.dhumbal.global.config.security;

public class AuthConstant {

    private AuthConstant() {
    }

    public static final String AUTH_HEADER_NAME = "Authorization";

    public static final String REFRESH_TOKEN_CLAIM = "X-REFRESH-TOKEN";
    public static final String USER_TYPE_CLAIM = "USER_TYPE_CLAIM";

    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String ANONYMOUS = "anonymous";
}

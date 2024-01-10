package com.fyp.dhumbal.global.util;

import com.fyp.dhumbal.global.config.security.CustomAuthentication;
import com.fyp.dhumbal.user.dal.UserType;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    private AuthUtil() {
    }

    public static String getLoggedInUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                (SecurityContextHolder.getContext().getAuthentication() instanceof CustomAuthentication authentication)) {
            return authentication.getUserId();

        }
        return null;
    }

    public static String getLoggedInUserName() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                (SecurityContextHolder.getContext().getAuthentication() instanceof CustomAuthentication authentication)) {
            return authentication.getName();

        }
        return null;
    }

    public static String getLoggedInUserRefreshTokenId() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                (SecurityContextHolder.getContext().getAuthentication() instanceof CustomAuthentication authentication)) {
            return authentication.getRefreshTokenId();

        }
        return null;
    }

    public static UserType getLoggedInUserType() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                (SecurityContextHolder.getContext().getAuthentication() instanceof CustomAuthentication authentication)) {
            return authentication.getUserType();

        }
        return null;
    }
}

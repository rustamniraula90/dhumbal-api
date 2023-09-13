package com.fyp.dhumbal.global.service;

import com.fyp.dhumbal.auth.rest.model.LoginResponse;
import com.fyp.dhumbal.global.config.security.CustomAuthentication;
import com.fyp.dhumbal.user.dal.UserEntity;

public interface TokenService {
    LoginResponse generateToken(UserEntity userEntity);

    LoginResponse refreshToken(String refreshToken);

    CustomAuthentication validateToken(String token);

    void deleteToken(String refreshTokenId);
}

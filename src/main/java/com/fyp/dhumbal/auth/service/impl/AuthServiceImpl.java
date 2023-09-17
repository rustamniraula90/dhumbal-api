package com.fyp.dhumbal.auth.service.impl;

import com.fyp.dhumbal.auth.rest.model.GoogleAuthRequest;
import com.fyp.dhumbal.auth.rest.model.LoginResponse;
import com.fyp.dhumbal.auth.rest.model.RefreshTokenRequest;
import com.fyp.dhumbal.auth.service.AuthService;
import com.fyp.dhumbal.global.sdk.GoogleSdk;
import com.fyp.dhumbal.global.service.TokenService;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final GoogleSdk googleSdk;
    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public LoginResponse authenticateGoogle(GoogleAuthRequest request) {
        GoogleSdk.GoogleUserDetail userDetail = googleSdk.verifyCredential(request.getCredential());
        UserEntity userEntity = userService.getOrCreateUser(userDetail);
        return tokenService.generateToken(userEntity);
    }

    @Override
    public LoginResponse authenticateGuest() {
        UserEntity userEntity = userService.createNewGuest();
        return tokenService.generateToken(userEntity);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        return tokenService.refreshToken(request.getToken());
    }

    @Override
    public void logout() {
        tokenService.deleteToken(AuthUtil.getLoggedInUserRefreshTokenId());
    }
}

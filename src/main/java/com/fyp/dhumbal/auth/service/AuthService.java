package com.fyp.dhumbal.auth.service;

import com.fyp.dhumbal.auth.rest.model.GoogleAuthRequest;
import com.fyp.dhumbal.auth.rest.model.LoginResponse;
import com.fyp.dhumbal.auth.rest.model.RefreshTokenRequest;

public interface AuthService {
    LoginResponse authenticateGoogle(GoogleAuthRequest request);

    LoginResponse authenticateGuest();

    LoginResponse refreshToken(RefreshTokenRequest request);
}

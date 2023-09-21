package com.fyp.dhumbal.auth.service;

import com.fyp.dhumbal.auth.rest.model.*;

public interface AuthService {
    LoginResponse authenticateGoogle(GoogleAuthRequest request);

    LoginResponse authenticateGuest();

    LoginResponse refreshToken(RefreshTokenRequest request);

    void logout();

    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}

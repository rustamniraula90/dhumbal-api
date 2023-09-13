package com.fyp.dhumbal.auth.rest;

import com.fyp.dhumbal.auth.rest.model.GoogleAuthRequest;
import com.fyp.dhumbal.auth.rest.model.LoginResponse;
import com.fyp.dhumbal.auth.rest.model.RefreshTokenRequest;
import com.fyp.dhumbal.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public LoginResponse googleAuth(@RequestBody GoogleAuthRequest request) {
        return authService.authenticateGoogle(request);
    }

    @PostMapping("/guest")
    public LoginResponse guestLogin() {
        return authService.authenticateGuest();
    }

    @PostMapping("/token/refresh")
    public LoginResponse guestLogin(@RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }
}

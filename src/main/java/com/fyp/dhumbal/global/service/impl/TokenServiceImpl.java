package com.fyp.dhumbal.global.service.impl;

import com.fyp.dhumbal.auth.rest.model.LoginResponse;
import com.fyp.dhumbal.global.config.security.AuthConstant;
import com.fyp.dhumbal.global.config.security.CustomAuthentication;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.ForbiddenException;
import com.fyp.dhumbal.global.property.JwtProperty;
import com.fyp.dhumbal.global.service.TokenService;
import com.fyp.dhumbal.global.util.JwtUtil;
import com.fyp.dhumbal.token.service.ActiveTokenService;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserRepository userRepository;
    private final JwtProperty jwtProperty;
    private final ActiveTokenService tokenService;


    @Override
    public LoginResponse generateToken(UserEntity userEntity) {
        String refreshTokenId = UUID.randomUUID().toString();
        String accessTokenId = UUID.randomUUID().toString();

        JwtUtil.JwtToken refreshToken = JwtUtil.Generator.generate(refreshTokenId,
                userEntity.getId(),
                new HashMap<>(),
                jwtProperty.getRefreshTokenExpiryInMinute(), jwtProperty.getKey());
        tokenService.saveRefreshToken(refreshTokenId, userEntity.getId(), refreshToken.getExpiry());

        JwtUtil.JwtToken accessToken = JwtUtil.Generator.generate(accessTokenId,
                userEntity.getId(),
                Collections.singletonMap(AuthConstant.REFRESH_TOKEN_CLAIM, refreshTokenId),
                jwtProperty.getRefreshTokenExpiryInMinute(), jwtProperty.getKey());
        LoginResponse token = new LoginResponse(userEntity.getId(), userEntity.getName(), accessToken, refreshToken);
        tokenService.saveAccessToken(accessTokenId, refreshTokenId, userEntity.getId(), accessToken.getExpiry());

        return token;
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        Claims claims = JwtUtil.Validator.parse(refreshToken, jwtProperty.getKey());
        if (!tokenService.existsById(claims.getId())) {
            throw new ForbiddenException(ErrorCodes.FORBIDDEN, "Token is no longer valid!!");
        }
        tokenService.deleteAccessTokens(claims.getId());
        UserEntity userEntity = userRepository.findById(claims.getSubject()).orElseThrow(() ->
                new ForbiddenException(ErrorCodes.BAD_REQUEST, "User doesn't exists!!"));
        JwtUtil.JwtToken accessToken = JwtUtil.Generator.generate(UUID.randomUUID().toString(),
                claims.getSubject(),
                Collections.singletonMap(AuthConstant.REFRESH_TOKEN_CLAIM, claims.getId()),
                jwtProperty.getRefreshTokenExpiryInMinute(), jwtProperty.getKey());
        return new LoginResponse(userEntity.getId(), userEntity.getName(), accessToken, new JwtUtil.JwtToken(refreshToken, claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
    }

    @Override
    public CustomAuthentication validateToken(String token) {
        if (token == null || token.isEmpty()) return null;
        Claims claims = JwtUtil.Validator.parse(token, jwtProperty.getKey());
        if (!tokenService.existsById(claims.getId())) {
            throw new ForbiddenException(ErrorCodes.FORBIDDEN, "Token is no longer valid!!");
        }
        UserEntity userEntity = userRepository.findById(String.valueOf(claims.getSubject())).orElseThrow(() -> new ForbiddenException(ErrorCodes.ACCESS_DENIED, "Invalid token!!!"));
        return new CustomAuthentication(claims.getId(), String.valueOf(claims.get(AuthConstant.REFRESH_TOKEN_CLAIM)), userEntity);

    }

    @Override
    public void deleteToken(String refreshTokenId) {
        tokenService.deleteTokens(refreshTokenId);
    }

}

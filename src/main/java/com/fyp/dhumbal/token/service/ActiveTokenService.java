package com.fyp.dhumbal.token.service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

public interface ActiveTokenService {

    void saveAccessToken(String tokenId, String refreshTokenId, String userId, LocalDateTime expiry);

    boolean existsById(String id);

    void saveRefreshToken(String tokenId, String userId, LocalDateTime expiry);

    void deleteTokens(String refreshTokenId);

    void deleteAccessTokens(String refreshTokenId);
}

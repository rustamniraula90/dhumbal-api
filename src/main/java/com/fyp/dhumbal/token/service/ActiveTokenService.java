package com.fyp.dhumbal.token.service;

import com.fyp.dhumbal.user.rest.model.UserSessionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ActiveTokenService {

    void saveAccessToken(String tokenId, String refreshTokenId, String userId, LocalDateTime expiry);

    boolean existsById(String id);

    void saveRefreshToken(String tokenId, String userId, LocalDateTime expiry);

    void deleteTokens(String refreshTokenId);

    void deleteAccessTokens(String refreshTokenId);

    List<UserSessionResponse> getUserSessions(String userId);

    void deleteSession(String id);
}

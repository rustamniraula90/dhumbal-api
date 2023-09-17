package com.fyp.dhumbal.token.service.impl;

import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.InternalServerException;
import com.fyp.dhumbal.token.entity.ActiveTokenEntity;
import com.fyp.dhumbal.token.entity.ActiveTokenType;
import com.fyp.dhumbal.token.mapper.ActiveTokenMapper;
import com.fyp.dhumbal.token.repository.ActiveTokenRepository;
import com.fyp.dhumbal.token.service.ActiveTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActiveTokenServiceImpl implements ActiveTokenService {
    private final ActiveTokenRepository activeTokenRepository;
    private final ActiveTokenMapper activeTokenMapper;

    @Override
    public void saveAccessToken(String tokenId, String refreshTokenId, String userId, LocalDateTime expiry) {
        ActiveTokenEntity refresh = activeTokenRepository.findById(refreshTokenId).orElseThrow(() -> new InternalServerException(ErrorCodes.INTERNAL_SERVER_ERROR, "Refresh token not found."));
        ActiveTokenEntity entity = activeTokenMapper.toEntity(tokenId, userId, expiry, ActiveTokenType.ACCESS);
        entity.setRefreshToken(refresh.getId());
        activeTokenRepository.save(entity);
    }

    @Override
    public boolean existsById(String id) {
        return activeTokenRepository.existsById(id);
    }

    @Override
    public void saveRefreshToken(String tokenId, String userId, LocalDateTime expiry) {
        activeTokenRepository.save(activeTokenMapper.toEntity(tokenId, userId, expiry, ActiveTokenType.REFRESH));
    }

    @Override
    public void deleteTokens(String refreshTokenId) {
        deleteAccessTokens(refreshTokenId);
        activeTokenRepository.deleteById(refreshTokenId);
    }

    @Override
    public void deleteAccessTokens( String refreshTokenId) {
        for (ActiveTokenEntity entity : activeTokenRepository.findByRefreshToken(refreshTokenId)) {
            if (entity.getTokenType() == ActiveTokenType.REFRESH) continue;
            activeTokenRepository.deleteById(entity.getId());
        }
    }
}

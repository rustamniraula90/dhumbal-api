package com.fyp.dhumbal.token.repository;

import com.fyp.dhumbal.token.entity.ActiveTokenEntity;
import com.fyp.dhumbal.token.entity.ActiveTokenType;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActiveTokenRepository extends KeyValueRepository<ActiveTokenEntity, String> {

    List<ActiveTokenEntity> findByRefreshToken(String refreshTokenId);

}

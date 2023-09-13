package com.fyp.dhumbal.token.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("active_token")
public class ActiveTokenEntity implements Serializable {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private ActiveTokenType tokenType;

    private LocalDateTime expiry;

    @Indexed
    private String refreshToken;

    @Indexed
    private String user;

    private String ip;

    private String userAgent;
}

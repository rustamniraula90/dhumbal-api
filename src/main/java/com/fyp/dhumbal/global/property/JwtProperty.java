package com.fyp.dhumbal.global.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "dhumbal.security.jwt")
public class JwtProperty {
    private String key;
    private Integer accessTokenExpiryInMinute;
    private Integer refreshTokenExpiryInMinute;
}

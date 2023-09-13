package com.fyp.dhumbal.global.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "dhumbal.security.google")
public class GoogleProperty {

    private String certUrl;
    private String clientId;
}

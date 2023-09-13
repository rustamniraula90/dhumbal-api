package com.fyp.dhumbal.global.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "dhumbal.security")
public class SecurityProperty {
    private List<String> corsAllowedOrigins;
    private List<String> corsAllowedMethods;
    private List<String> permitUrls;
    private JwtProperty jwt;
    private GoogleProperty google;
}

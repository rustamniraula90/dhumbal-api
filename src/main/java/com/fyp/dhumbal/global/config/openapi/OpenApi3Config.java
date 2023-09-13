package com.fyp.dhumbal.global.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApi3Config {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.version}")
    private String version;
    @Value("${spring.application.description}")
    private String description;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(getInfo());
    }

    private Info getInfo() {
        return new Info().title(this.appName).version(this.version).description(description);
    }
}

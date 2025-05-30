package com.fyp.dhumbal.global.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String ROOM_TOPIC = "/room";
    public static final String GAME_TOPIC = "/game";
    public static final String USER_TOPIC = "/user";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(ROOM_TOPIC, GAME_TOPIC, USER_TOPIC);
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/dhumbal-websocket").setAllowedOrigins("*");
        registry.addEndpoint("/dhumbal-websocket").setAllowedOrigins("*").withSockJS();
    }
}

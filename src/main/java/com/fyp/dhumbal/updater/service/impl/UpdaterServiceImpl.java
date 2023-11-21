package com.fyp.dhumbal.updater.service.impl;

import com.fyp.dhumbal.updater.model.UpdateType;
import com.fyp.dhumbal.updater.model.UpdaterPayload;
import com.fyp.dhumbal.updater.service.UpdaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static com.fyp.dhumbal.global.config.websocket.WebSocketConfig.PLAYER_TOPIC;
import static com.fyp.dhumbal.global.config.websocket.WebSocketConfig.ROOM_TOPIC;

@Service
@RequiredArgsConstructor
public class UpdaterServiceImpl implements UpdaterService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void updateRoom(String id, UpdateType type, Object payload) {
        this.simpMessagingTemplate.convertAndSend(ROOM_TOPIC + "/" + id, new UpdaterPayload(type, payload));
    }

    @Override
    public void updatePlayer(String id, UpdateType type, Object payload) {
        this.simpMessagingTemplate.convertAndSend(PLAYER_TOPIC + "/" + id, new UpdaterPayload(type, payload));
    }
}

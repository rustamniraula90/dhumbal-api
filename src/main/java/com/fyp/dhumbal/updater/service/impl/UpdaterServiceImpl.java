package com.fyp.dhumbal.updater.service.impl;

import com.fyp.dhumbal.updater.model.UpdateType;
import com.fyp.dhumbal.updater.model.UpdaterPayload;
import com.fyp.dhumbal.updater.service.UpdaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static com.fyp.dhumbal.global.config.websocket.WebSocketConfig.*;

@Service
@RequiredArgsConstructor
public class UpdaterServiceImpl implements UpdaterService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void updateRoom(String id, UpdateType type, Object payload) {
        this.simpMessagingTemplate.convertAndSend(ROOM_TOPIC + "/" + id, new UpdaterPayload(type, payload));
    }

    @Override
    public void updateGame(String id, UpdateType type, Object payload) {
        this.simpMessagingTemplate.convertAndSend(GAME_TOPIC + "/" + id, new UpdaterPayload(type, payload));
    }

    @Override
    public void updateUser(String id, UpdateType type, Object payload) {
        this.simpMessagingTemplate.convertAndSend(USER_TOPIC + "/" + id, new UpdaterPayload(type, payload));

    }
}

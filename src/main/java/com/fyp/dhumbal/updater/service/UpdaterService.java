package com.fyp.dhumbal.updater.service;

import com.fyp.dhumbal.updater.model.UpdateType;

public interface UpdaterService {
    void updateRoom(String id, UpdateType type, Object payload);
    void updatePlayer(String id, UpdateType type, Object payload);
}

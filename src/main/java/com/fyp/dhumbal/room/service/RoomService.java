package com.fyp.dhumbal.room.service;

import com.fyp.dhumbal.agent.AgentConstant;
import com.fyp.dhumbal.room.rest.model.CreateRoomRequest;
import com.fyp.dhumbal.room.rest.model.RoomResponse;

public interface RoomService {
    RoomResponse createRoom(CreateRoomRequest request);

    RoomResponse joinRoom(String code);

    RoomResponse joinRandomRoom();

    void leaveRoom(String id);

    RoomResponse startRoom(String roomId);

    RoomResponse getRoomById(String roomId);

    RoomResponse addBot(String roomId,Integer level);

    void removeFromRoom(String id, String userId);
}

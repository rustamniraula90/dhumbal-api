package com.fyp.dhumbal.room.service;

import com.fyp.dhumbal.room.rest.model.CreateRoomRequest;
import com.fyp.dhumbal.room.rest.model.RoomResponse;

public interface RoomService {
    RoomResponse createRoom(CreateRoomRequest request);

    RoomResponse joinRoom(String code);

    RoomResponse joinRandomRoom();

    RoomResponse leaveRoom(String code);

    RoomResponse startRoom(String roomId);

    RoomResponse getRoomById(String roomId);
}

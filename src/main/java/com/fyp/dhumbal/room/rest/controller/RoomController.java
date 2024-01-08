package com.fyp.dhumbal.room.rest.controller;

import com.fyp.dhumbal.agent.AgentConstant;
import com.fyp.dhumbal.room.rest.model.CreateRoomRequest;
import com.fyp.dhumbal.room.rest.model.RoomResponse;
import com.fyp.dhumbal.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public RoomResponse getRoom(@PathVariable("roomId") String roomId) {
        return roomService.getRoomById(roomId);
    }

    @PostMapping
    public RoomResponse createRoom(@RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }

    @PostMapping("/{id}/bot")
    public RoomResponse addBot(@PathVariable("id") String id, @RequestParam("level") Integer level) {
        return roomService.addBot(id, level);
    }

    @PostMapping("/join/random")
    public RoomResponse joinRandomRoom() {
        return roomService.joinRandomRoom();
    }

    @PostMapping("/{code}/join")
    public RoomResponse joinRoom(@PathVariable("code") String code) {
        return roomService.joinRoom(code);
    }

    @DeleteMapping("/{id}/leave")
    public void leaveRoom(@PathVariable("id") String id) {
        roomService.leaveRoom(id);
    }

    @DeleteMapping("/{id}/remove/{userId}")
    public void removeFromRoom(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        roomService.removeFromRoom(id, userId);
    }

    @PostMapping("/{id}/start")
    public RoomResponse startRoom(@PathVariable("id") String roomId) {
        return roomService.startRoom(roomId);
    }
}

package com.fyp.dhumbal.friend.rest.controller;

import com.fyp.dhumbal.friend.rest.model.FriendResponse;
import com.fyp.dhumbal.friend.rest.model.FriendRequest;
import com.fyp.dhumbal.friend.rest.model.InviteFriendRequest;
import com.fyp.dhumbal.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public FriendResponse sendFriendRequest(@RequestBody FriendRequest request) {
        return friendService.sendFriendRequest(request);
    }

    @PutMapping
    public void acceptFriendRequest(@RequestBody FriendRequest request) {
        friendService.acceptFriendRequest(request);
    }

    @DeleteMapping
    public void removeFriend(@RequestBody FriendRequest request) {
        friendService.removeFriend(request);
    }

    @GetMapping
    public List<FriendResponse> getFriends() {
        return friendService.getFriends();
    }

    @GetMapping("/online")
    public List<FriendResponse> getOnlineFriends() {
        return friendService.getFriendsByOnline(true);
    }

    @GetMapping("/requests")
    public List<FriendResponse> getFriendRequests() {
        return friendService.getFriendRequest();
    }

    @PostMapping("/invite")
    public void inviteFriendToRoom(@RequestBody InviteFriendRequest request) {
        friendService.inviteFriendToRoom(request);
    }
}

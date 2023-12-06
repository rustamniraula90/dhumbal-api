package com.fyp.dhumbal.friend.service;

import com.fyp.dhumbal.friend.rest.model.FriendResponse;
import com.fyp.dhumbal.friend.rest.model.FriendRequest;
import com.fyp.dhumbal.friend.rest.model.InviteFriendRequest;

import java.util.List;

public interface FriendService {
    FriendResponse sendFriendRequest(FriendRequest request);

    List<FriendResponse> getFriendRequest();

    List<FriendResponse> getFriends();

    void removeFriend(FriendRequest request);

    void acceptFriendRequest(FriendRequest request);

    List<FriendResponse> getFriendsByOnline(boolean online);

    void inviteFriendToRoom(InviteFriendRequest request);
}

package com.fyp.dhumbal.friend.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteFriendRequest {
    private String friendId;
    private String roomId;
}

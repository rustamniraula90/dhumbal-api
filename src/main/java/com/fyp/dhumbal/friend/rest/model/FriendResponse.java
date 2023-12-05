package com.fyp.dhumbal.friend.rest.model;

import com.fyp.dhumbal.friend.dal.FriendshipStatus;
import com.fyp.dhumbal.user.rest.model.UserResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendResponse {
    private UserResponse user;
    private FriendshipStatus status;
}

package com.fyp.dhumbal.user.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fyp.dhumbal.friend.dal.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Boolean online;
    private FriendStatus friendshipStatus;

    public UserResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public enum FriendStatus {
        FRIEND, NOT_FRIEND, REQUEST_SENT, REQUEST_RECEIVED
    }
}

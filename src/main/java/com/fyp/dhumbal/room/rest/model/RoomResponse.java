package com.fyp.dhumbal.room.rest.model;

import com.fyp.dhumbal.user.rest.model.UserResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomResponse {
    private String id;
    private String code;
    private UserResponse owner;
}

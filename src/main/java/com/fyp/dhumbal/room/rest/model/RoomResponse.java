package com.fyp.dhumbal.room.rest.model;

import com.fyp.dhumbal.room.dal.RoomStatusEnum;
import com.fyp.dhumbal.user.rest.model.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomResponse {
    private String id;
    private String code;
    private UserResponse owner;
    private RoomStatusEnum status;
    private List<UserResponse> members;
    private int easyAgent = 0;
    private int hardAgent = 0;
}

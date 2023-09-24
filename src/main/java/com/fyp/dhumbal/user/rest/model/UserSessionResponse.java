package com.fyp.dhumbal.user.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSessionResponse {
    private String id;
    private String ip;
    private String userAgent;
    private String loggedInTime;
}

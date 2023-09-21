package com.fyp.dhumbal.auth.rest.model;

import com.fyp.dhumbal.global.util.JwtUtil;
import com.fyp.dhumbal.user.dal.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String userId;
    private String name;
    private UserType userType;
    private JwtUtil.JwtToken accessToken;
    private JwtUtil.JwtToken refreshToken;
}

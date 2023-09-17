package com.fyp.dhumbal.user.rest.controller;

import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.user.rest.model.UserResponse;
import com.fyp.dhumbal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/current")
    public UserResponse getCurrentUser() {
        return userService.getById(AuthUtil.getLoggedInUserId());
    }
}

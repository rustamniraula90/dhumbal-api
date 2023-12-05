package com.fyp.dhumbal.user.rest.controller;

import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.global.util.ResourceUtil;
import com.fyp.dhumbal.token.service.ActiveTokenService;
import com.fyp.dhumbal.user.rest.model.UserResponse;
import com.fyp.dhumbal.user.rest.model.UserSessionResponse;
import com.fyp.dhumbal.user.service.UserService;
import com.fyp.dhumbal.user.rest.model.GetUserProfileResponse;
import com.fyp.dhumbal.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserProfileService userProfileService;
    private final ActiveTokenService tokenService;

    @Value("classpath:verify_page.html")
    private Resource resource;

    @GetMapping("/current")
    public UserResponse getCurrentUser() {
        return userService.getById(AuthUtil.getLoggedInUserId(), true);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable("id") String id) {
        return userService.getById(id, false);
    }

    @GetMapping("/profile")
    public GetUserProfileResponse getCurrentUserProfile() {
        return userProfileService.getUserProfileById(AuthUtil.getLoggedInUserId());
    }

    @GetMapping("/profile/{id}")
    public GetUserProfileResponse getUserProfile(@PathVariable("id") String id) {
        return userProfileService.getUserProfileById(id);
    }

    @GetMapping("/session")
    public List<UserSessionResponse> getCurrentUserSession() {
        return tokenService.getUserSessions(AuthUtil.getLoggedInUserId());
    }

    @DeleteMapping("/session/{id}")
    public void deleteUserSession(@PathVariable("id") String id) {
        tokenService.deleteSession(id);
    }

    @GetMapping(value = "/verify/{id}/{code}", produces = "text/html")
    public String verifyCode(@PathVariable("id") String id, @PathVariable("code") String code) {
        try {
            userService.verifyUser(id, code);
            return sendResponse("Email verified successfully!!!");
        } catch (BadRequestException e) {
            return sendResponse(e.getMessage());
        } catch (Exception e) {
            return sendResponse("Something went wrong, Please try again later!!!");
        }
    }

    @MessageMapping("/user")
    public void send(Message<String> userId) {
        userService.setOnline(userId.getPayload(), true);
    }

    private String sendResponse(String message) {
        String page = ResourceUtil.asString(resource);
        page = page.replace("{message}", message);
        return page;
    }

}

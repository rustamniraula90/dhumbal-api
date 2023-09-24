package com.fyp.dhumbal.user.service;

import com.fyp.dhumbal.auth.rest.model.LoginRequest;
import com.fyp.dhumbal.auth.rest.model.LoginResponse;
import com.fyp.dhumbal.auth.rest.model.RegisterRequest;
import com.fyp.dhumbal.global.sdk.GoogleSdk;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.rest.model.UserResponse;

public interface UserService {
    UserEntity getOrCreateUser(GoogleSdk.GoogleUserDetail googleUserDetail);

    UserEntity createNewGuest();

    UserResponse getById(String loggedInUserId, boolean includeEmail);

    UserEntity loginUser(LoginRequest request);

    UserEntity registerUser(RegisterRequest request);
}

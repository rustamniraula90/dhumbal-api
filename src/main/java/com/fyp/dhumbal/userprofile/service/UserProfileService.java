package com.fyp.dhumbal.userprofile.service;

import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.rest.model.GetUserProfileResponse;

public interface UserProfileService {
    GetUserProfileResponse getUserProfileById(String loggedInUserId);

    void createUserProfile(UserEntity userEntity);

    void updateStatus(String userId, boolean winner);

}

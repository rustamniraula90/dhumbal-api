package com.fyp.dhumbal.userprofile.mapper;

import com.fyp.dhumbal.userprofile.dal.UserProfileEntity;
import com.fyp.dhumbal.user.rest.model.GetUserProfileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    GetUserProfileResponse toResponse(UserProfileEntity userProfileEntity);

}
